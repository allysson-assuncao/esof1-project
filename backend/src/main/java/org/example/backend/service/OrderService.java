package org.example.backend.service;

import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderFilterDTO;
import org.example.backend.dto.ProductDTO;
import org.example.backend.model.Order;
import org.example.backend.model.User;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSpecificationService orderSpecificationService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderSpecificationService orderSpecificationService, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderSpecificationService = orderSpecificationService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Todo...
    public boolean registerOrder(String request){
        return false;
    }

    /*public Page<OrderDTO> getOOrdersByFilters(OrderFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction, User user) {
        Specification<Order> specification = this.orderSpecificationService.getOrderSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<Order> ordersPage = this.orderRepository.findAll(specification, pageable);

        return ordersPage.map(orders -> OrderDTO.builder()
                .id(orders.getId())
                .amount(orders.getAmount())
                .observation(orders.getObservation())
                .status(orders.getStatus())
                .orderedTime(orders.getOrderedTime())
                .additionalOrders(orders.getAdditionalOrders().stream()
                        .map(order -> new OrderDTO(
                                order.getId(),
                        ))
                        .collect(Collectors.toList()))
                .guestTabId(orders.getGuestTab().getId())
                .productName(orders.getProduct().getName())
                .price(orders.getProduct().getPrice())
                .waiterName(this.userRepository.findBy)
                .build());
    }*/

}
