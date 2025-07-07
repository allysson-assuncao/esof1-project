package org.example.backend.dto.Order;

import lombok.Builder;
import org.example.backend.model.Order;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record OrderDTO(Long id,
                       int amount,
                       OrderStatus status,
                       String observation,
                       LocalDateTime orderedTime,
                       LocalDateTime preparationTime,
                       LocalDateTime readyTime,
                       LocalDateTime closedTime,
                       Set<Long> additionalOrders,
                       String productName,
                       double productUnitPrice,
                       String waiterName) {

    public OrderDTO(Order order) {
        this(
                order.getId(),
                order.getAmount(),
                order.getStatus(),
                order.getObservation(),
                order.getOrderedTime(),
                order.getPreparationTime(),
                order.getReadyTime(),
                order.getClosedTime(),
                order.getAdditionalOrders()
                        .stream().map(Order::getId).collect(Collectors.toSet()),
                order.getProduct().getName(),
                order.getProduct().getPrice(),
                order.getWaiter().getName()
        );
    }
}
