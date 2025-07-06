package org.example.backend.service;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.Order.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada"));


        Order parent = null;
        if (request.parentOrderId() != null) {
            parent = orderRepository.findById(request.parentOrderId())
                    .orElse(null);
        }

        LocalDateTime now = LocalDateTime.now();

        for (OrderItemDTO item : request.items()) {
            Order order = Order.builder()
                    .amount(item.amount())
                    .observation(item.observation())
                    .status(OrderStatus.SENT)
                    .orderedTime(now)
                    .parentOrder(parent)
                    .guestTab(guestTab)
                    .product(productRepository.findById(item.productId()).orElseThrow())
                    .waiter(waiter)
                    .build();

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

        Page<FilteredOrderKanbanDTO> dtoPage = orderPage.map(this::mapOrderToDto);
        return new FilteredPageDTO<>(dtoPage.getContent(), dtoPage.getTotalPages()/*, dtoPage.getTotalElements()*/);
    }

    private FilteredOrderKanbanDTO mapOrderToDto(Order order) {
        return FilteredOrderKanbanDTO.builder()
                .id(order.getId())
                .productName(order.getProduct().getName()) // Assumindo que Product tem um getName()
                .amount(order.getAmount())
                .observation(order.getObservation())
                .orderedTime(order.getOrderedTime())
                .status(order.getStatus())
                .workstationName(order.getWorkstation() != null ? order.getWorkstation().getName() : "N/A") // Assumindo getName()
                .additionalOrders(
                        order.getAdditionalOrders() != null ?
                                order.getAdditionalOrders().stream()
                                        .map(this::mapOrderToDto)
                                        .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .build();
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
