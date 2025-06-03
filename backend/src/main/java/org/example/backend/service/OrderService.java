package org.example.backend.service;

import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderFilterDTO;
import org.example.backend.model.Order;
import org.example.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public Page<OrderDTO> getOrdersByFilters(OrderFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<Order> specification = this.orderSpecificationService.getOrderSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<Order> ordersPage = this.orderRepository.findAll(specification, pageable);

        return ordersPage.map(this::convertToOrderDTO);
    }

    private OrderDTO convertToOrderDTO(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .orderId(order.getId())
                .amount(order.getAmount())
                .observation(order.getObservation())
                .orderStatus(order.getStatus() != null ? order.getStatus() : null)
                .orderedTime(order.getOrderedTime())
                .orderTotalPrice(order.getProduct() != null ? order.getAmount() * order.getProduct().getPrice() : 0.0)
                .additionalOrders(
                        order.getAdditionalOrders() != null
                                ? order.getAdditionalOrders().stream()
                                .map(Order::getId)
                                .collect(Collectors.toSet())
                                : null
                )
                .productName(order.getProduct() != null ? order.getProduct().getName() : null)
                .productUnitPrice(order.getProduct() != null ? order.getProduct().getPrice() : 0.0)
                .guestTabId(order.getGuestTab() != null ? order.getGuestTab().getId() : null)
                .guestTabStatus(order.getGuestTab() != null && order.getGuestTab().getStatus() != null ? order.getGuestTab().getStatus() : null)
                .guestTabTimeOpened(order.getGuestTab() != null ? order.getGuestTab().getTimeOpened() : null)
                .waiterName(order.getWaiter() != null ? order.getWaiter().getName() : null)
                .localTableNumber(order.getGuestTab() != null && order.getGuestTab().getLocalTable() != null ? order.getGuestTab().getLocalTable().getNumber() : 0)
                .build();
    }

}
