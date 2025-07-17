package org.example.backend.dto.GuestTab;

import org.example.backend.dto.Payment.PaymentSummaryDTO;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GuestTabFilterDTO(UUID tableId, List<Long> guestTabIds, List<Long> orderIds, List<OrderStatus> orderStatuses, List<GuestTabStatus> guestTabStatuses, Double minPrice, Double maxPrice, LocalDateTime startTime, LocalDateTime endTime, List<UUID> waiterIds, String productName) { }
