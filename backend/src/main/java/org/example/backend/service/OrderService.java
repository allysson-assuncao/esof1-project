package org.example.backend.service;

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

import java.time.LocalDateTime;
import java.util.Optional;

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
    public boolean registerOrder(OrderRequestDTO request) {
        User waiter = userRepository.findByEmail(request.userEmail()).orElseThrow();
        GuestTab guestTab = guestTabRepository.findById(request.guestTabId()).orElseThrow();
        Product product = productRepository.findById(request.productId()).orElseThrow();
        Optional<Order> parentOrder = Optional.empty();
        if(request.parentOrderId() != null) {
            parentOrder = orderRepository.findById(request.parentOrderId());
        }

        Order order = Order.builder()
                .amount(request.amount())
                .observation(request.observation())
                .parentOrder(parentOrder.orElse(null))
                .guestTab(guestTab)
                .product(product)
                .status(OrderStatus.IN_PREPARE)
                .orderedTime(LocalDateTime.now())
                .waiter(waiter)
                .build();

        orderRepository.save(order);

        return true;
    }

}
