package org.example.backend.service;

import org.example.backend.dto.SimpleOrderDTO;
import org.example.backend.model.Order;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // Test Product id: 408da554-1205-45df-8608-54f5fad1d365
    public boolean registerOrder(OrderRequestDTO request) {
        User waiter = userRepository.findByEmail(request.userEmail()).orElseThrow();
        GuestTab guestTab = guestTabRepository.findById(request.guestTabId()).orElseThrow();
        Product product = productRepository.findById(request.productId()).orElseThrow();
        Optional<Order> parentOrder = null;
        try {
            parentOrder = orderRepository.findById(request.parentOrderId());
        } catch (NullPointerException e) {
            parentOrder = Optional.empty();
        }

        Order order = Order.builder()
                .amount(request.amount())
                .observation(request.observation())
                .parentOrder(parentOrder.orElse(null))
                .guestTab(guestTab)
                .product(product)
                .waiter(waiter)
                .build();

        orderRepository.save(order);

        return true;
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
}
