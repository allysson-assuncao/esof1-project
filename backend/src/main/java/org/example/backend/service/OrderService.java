package org.example.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.backend.dto.DetailedOrderDTO;
import org.example.backend.dto.OrderRequestDTO;
import org.example.backend.dto.SimpleOrderDTO;
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

    @Transactional
    public List<DetailedOrderDTO> getQueue() {
        return orderRepository.findAll().stream()
                .map(this::convertToDetailedOrderDTO)
                .collect(Collectors.toList());
    }

    // Test Product id: 408da554-1205-45df-8608-54f5fad1d365
    @Transactional
    public boolean registerOrder(OrderRequestDTO request) {
        User waiter = userRepository.findByEmail(request.userEmail())
                .orElseThrow(() -> new EntityNotFoundException("Garçom não encontrado"));

        GuestTab guestTab = guestTabRepository.findById(request.guestTabId())
                .orElseThrow(() -> new EntityNotFoundException("Mesa não encontrada"));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Order parent = null;
        if (request.parentOrderId() != null) {
            parent = orderRepository.findById(request.parentOrderId())
                    .orElse(null);
        }

        Order order = Order.builder()
                .amount(request.amount())
                .observation(request.observation())
                .status(OrderStatus.IN_PREPARE)
                .orderedTime(LocalDateTime.now())
                .parentOrder(parent)
                .guestTab(guestTab)
                .product(product)
                .waiter(waiter)
                .build();

        orderRepository.save(order);

        return true;
    }


    public List<DetailedOrderDTO> selectOrdersByGuestTabId (Long guestTabId) {
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
}
