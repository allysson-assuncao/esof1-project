package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderRequestDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.model.Order;
import org.example.backend.model.Product;
import org.example.backend.model.User;
import org.example.backend.model.enums.OrderStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    // Test Product id: 35392b1a-f4e9-4bf4-8b9a-66690b19d527
    // Cria e registra um pedido a partir de um OrderRequestDTO cujos parametros são passados na requisição
    @Transactional
    public boolean registerOrder(OrderRequestDTO request) {
        Order order = convertRequestToOrder(request);
        orderRepository.save(order);
        return true;
    }

    //Converte uma OrderRequestDTO em um objeto Order, para ser salvo no banco
    public Order convertRequestToOrder(OrderRequestDTO order) {
        User waiter = userRepository.findByEmail(order.userEmail()).orElseThrow();
        GuestTab guestTab = guestTabRepository.findById(order.guestTabId()).orElseThrow();
        Product product = productRepository.findById(order.productId()).orElseThrow();
        Optional<Order> parentOrder = Optional.empty();
        if(order.parentOrderId() != null) {
            parentOrder = orderRepository.findById(order.parentOrderId());
        }
        return Order.builder()
                .amount(order.amount())
                .observation(order.observation())
                .parentOrder(parentOrder.orElse(null))
                .guestTab(guestTab)
                .product(product)
                .status(OrderStatus.IN_PREPARE)
                .orderedTime(LocalDateTime.now())
                .waiter(waiter)
                .build();
    }

    //Retorna lista com todos os pedidos
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = orderRepository.findAll().stream()
                .map(this::convertToOrderDTO).toList();

        return orders;
    }

    // Busca todos os pedidos de uma comanda (GuestTab)
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByGuestTabId(Long guestTabId) {
        GuestTab guestTab = guestTabRepository.findById(guestTabId)
                .orElseThrow(() -> new EntityNotFoundException("Comanda não encontrada"));

        return guestTab.getOrders().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }


    private OrderDTO convertToOrderDTO(Order order) {
        String productName = order.getProduct() != null ? order.getProduct().getName() : null;
        double productUnitPrice = order.getProduct() != null ? order.getProduct().getPrice() : 0.0;
        String waiterName = order.getWaiter() != null ? order.getWaiter().getName() : null;

        Set<Long> additionalOrderIds = order.getAdditionalOrders() != null
                ? order.getAdditionalOrders().stream().map(Order::getId).collect(Collectors.toSet())
                : Set.of();

        return OrderDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .observation(order.getObservation())
                .orderedTime(order.getOrderedTime())
                .additionalOrders(additionalOrderIds)
                .productName(productName)
                .productUnitPrice(productUnitPrice)
                .waiterName(waiterName)
                .build();
    }
}
