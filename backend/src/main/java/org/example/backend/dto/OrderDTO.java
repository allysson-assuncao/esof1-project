package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderDTO(Long orderId, int amount, String observation, OrderStatus orderStatus, LocalDateTime orderedTime, double orderTotalPrice, Set<Long> additionalOrders, String productName, double productUnitPrice, Long guestTabId, GuestTabStatus guestTabStatus, LocalDateTime guestTabTimeOpened, String waiterName, int localTableNumber) { }
