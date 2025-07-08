package org.example.backend.service;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.Order.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.example.backend.model.GuestTab;
import org.example.backend.model.Order;
import org.example.backend.model.User;
import org.example.backend.model.enums.OrderStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final GuestTabRepository guestTabRepository;
    private final ProductRepository productRepository;
    private final OrderSpecificationService orderSpecificationService;
    private static final List<OrderStatus> STATUS_FLOW = List.of(
            OrderStatus.SENT,
            OrderStatus.IN_PREPARE,
            OrderStatus.READY,
            OrderStatus.DELIVERED
    );

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, GuestTabRepository guestTabRepository, ProductRepository productRepository, OrderSpecificationService orderSpecificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.guestTabRepository = guestTabRepository;
        this.productRepository = productRepository;
        this.orderSpecificationService = orderSpecificationService;

    }

    @Transactional
    public List<DetailedOrderDTO> getQueue() {
        return orderRepository.findAll().stream()
                .map(this::convertToDetailedOrderDTO)
                .collect(Collectors.toList());
    }

    // Test Product id: 408da554-1205-45df-8608-54f5fad1d365
    @Transactional
    public boolean registerOrder(OrderRequestDTO request) {
        User waiter = userRepository.findByEmail(request.waiterEmail())
                .orElseThrow(() -> new EntityNotFoundException("Garçom não encontrado"));

        GuestTab guestTab = guestTabRepository.findById(request.guestTabId())
                .orElseThrow(() -> new EntityNotFoundException("Comanda não encontrada"));

        Order parentOrder = null;
        if (request.parentOrderId() != null) {
            parentOrder = orderRepository.findById(request.parentOrderId())
                    .orElseThrow(() -> new EntityNotFoundException("Pedido pai não encontrado com o ID: " + request.parentOrderId()));
        }

        LocalDateTime now = LocalDateTime.now();

        for (OrderItemDTO item : request.items()) {
            Order order = Order.builder()
                    .amount(item.amount())
                    .observation(item.observation())
                    .status(OrderStatus.SENT)
                    .orderedTime(now)
                    .guestTab(guestTab)
                    .product(productRepository.findById(item.productId()).orElseThrow())
                    .waiter(waiter)
                    .build();

            if (parentOrder != null) {
                parentOrder.addAdditionalOrder(order);
            }

            orderRepository.save(order);
        }

        return true;
    }


    public List<DetailedOrderDTO> selectOrdersByGuestTabId(Long guestTabId) {
        return orderRepository.findByGuestTabId(guestTabId).stream()
                .map(this::convertToDetailedOrderDTO)
                .collect(Collectors.toList());

    }

    public List<SimpleOrderDTO> selectOrdersByLocalTableId(UUID localTableID) {
        return this.orderRepository.findByGuestTabLocalTableId(localTableID).stream()
                .map(this::convertToSimpleOrderDTO)
                .collect(Collectors.toList());
    }

    private SimpleOrderDTO convertToSimpleOrderDTO(Order order) {
        if (order == null) return null;
        return SimpleOrderDTO.builder()
                .id(order.getId())
                .build();
    }

    private DetailedOrderDTO convertToDetailedOrderDTO(Order order) {
        if (order == null) return null;


        return DetailedOrderDTO.builder()
                .id(order.getId())
                .tableNumber(order.getGuestTab().getLocalTable().getNumber())
                .guestName(order.getGuestTab().getGuestName())
                .productName(order.getProduct().getName())
                .waiterName(order.getWaiter().getName())
                .amount(order.getAmount())
                .price(order.getProduct().getPrice())
                .timeOrdered(order.getOrderedTime())
                .build();
    }

    @Transactional
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public KanbanOrdersDTO getOrdersForKanban(OrderKanbanFilterDTO filter, int page, int size, String orderBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        System.out.println(pageable);

        FilteredPageDTO<FilteredOrderKanbanDTO> sentOrders = findOrdersByStatus(filter, OrderStatus.SENT, pageable);
        FilteredPageDTO<FilteredOrderKanbanDTO> inPrepareOrders = findOrdersByStatus(filter, OrderStatus.IN_PREPARE, pageable);
        FilteredPageDTO<FilteredOrderKanbanDTO> readyOrders = findOrdersByStatus(filter, OrderStatus.READY, pageable);

        return KanbanOrdersDTO.builder()
                .sentOrders(sentOrders)
                .inPrepareOrders(inPrepareOrders)
                .readyOrders(readyOrders)
                .build();
    }

    private FilteredPageDTO<FilteredOrderKanbanDTO> findOrdersByStatus(OrderKanbanFilterDTO filter, OrderStatus status, Pageable pageable) {
        Specification<Order> spec = orderSpecificationService.getOrderKanbanSpecification(filter, status);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        System.out.println(orderPage.getTotalElements());

        Page<FilteredOrderKanbanDTO> dtoPage = orderPage.map(this::mapOrderToDto);
        return new FilteredPageDTO<>(dtoPage.getContent(), dtoPage.getTotalPages()/*, dtoPage.getTotalElements()*/);
    }

    private FilteredOrderKanbanDTO mapOrderToDto(Order order) {
        if (order == null) {
            return null;
        }

        String productName = (order.getProduct() != null) ? order.getProduct().getName() : "N/A";
        String workstationName = (order.getWorkstation() != null) ? order.getWorkstation().getName() : "N/A";

        List<FilteredOrderKanbanDTO> additionalOrdersDto = (order.getAdditionalOrders() != null)
                ? order.getAdditionalOrders().stream()
                .map(this::mapOrderToDto)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return FilteredOrderKanbanDTO.builder()
                .id(order.getId())
                .productName(productName)
                .amount(order.getAmount())
                .observation(order.getObservation())
                .orderedTime(order.getOrderedTime())
                .status(order.getStatus())
                .workstationName(workstationName)
                .additionalOrders(additionalOrdersDto)
                .build();
    }

    @Transactional
    public void advanceStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        OrderStatus currentStatus = order.getStatus();
        OrderStatus nextStatus = getRelativeStatus(currentStatus, true);

        if (nextStatus == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já está no status final, não pode regredir");
        }

        updateStatusAndTimestamp(order, nextStatus);
        orderRepository.save(order);
    }

    @Transactional
    public void regressStatus(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        OrderStatus currentStatus = order.getStatus();
        OrderStatus nextStatus = getRelativeStatus(currentStatus, false);

        if (nextStatus == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já está no status final, não pode regredir");
        }

        updateStatusAndTimestamp(order, nextStatus);
        orderRepository.save(order);
    }

    private OrderStatus getRelativeStatus(OrderStatus current, boolean forward) {
        int index = STATUS_FLOW.indexOf(current);
        if (index == -1) return null;

        int newIndex = index + (forward ? 1 : -1);
        if (newIndex < 0 || newIndex >= STATUS_FLOW.size()) return null;

        return STATUS_FLOW.get(newIndex);
    }

    private void updateStatusAndTimestamp(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case SENT -> order.setOrderedTime(now);
            case IN_PREPARE -> order.setPreparationTime(now);
            case READY -> order.setReadyTime(now);
            case DELIVERED, CANCELED -> order.setClosedTime(now);
        }

        // Atualizar pedidos adicionais
        if (order.getAdditionalOrders() != null) {
            for (Order additional : order.getAdditionalOrders()) {
                additional.setStatus(newStatus);
                switch (newStatus) {
                    case SENT -> additional.setOrderedTime(now);
                    case IN_PREPARE -> additional.setPreparationTime(now);
                    case READY -> additional.setReadyTime(now);
                    case DELIVERED, CANCELED -> additional.setClosedTime(now);
                }
            }
        }
    }

    /*@Transactional
    public List<OrderDTO> getOrdersInPrepareAndBar() {
        List<Order> orders =
                orderRepository.findByStatusAndProduct_Destination(OrderStatus.IN_PREPARE, ProductDestination.BAR).orElseThrow();

        return orders.stream().map(OrderDTO::new).toList();
    }

    public List<OrderDTO> getOrdersInPrepareAndKitchen() {
        List<Order> orders = orderRepository.findByStatusAndProduct_Destination(OrderStatus.IN_PREPARE, ProductDestination.KITCHEN).orElseThrow();
        return orders.stream().map(OrderDTO::new).toList();
    }*/
}
