package org.example.backend.service;

import org.example.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSpecificationService orderSpecificationService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderSpecificationService orderSpecificationService) {
        this.orderRepository = orderRepository;
        this.orderSpecificationService = orderSpecificationService;
    }

    // Todo...
    public boolean registerOrder(String request) {
        return false;
    }

}
