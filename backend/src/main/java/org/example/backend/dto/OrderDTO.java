package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderDTO(Long guestTabId, GuestTabStatus guestTabStatus, LocalDateTime guestTabTimeOpened, double totalPrice, Set<Long> additionalOrders, Long orderId, int amount, OrderStatus orderStatus, String observation, LocalDateTime orderedTime, String productName, double productUnitPrice, String waiterName, int localTableNumber) { }
