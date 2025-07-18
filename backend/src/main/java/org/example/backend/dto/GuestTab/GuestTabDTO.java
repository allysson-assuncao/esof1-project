package org.example.backend.dto.GuestTab;

import lombok.Builder;
import org.example.backend.dto.Order.OrderGroupDTO;
import org.example.backend.dto.Payment.PaymentSummaryDTO;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record GuestTabDTO(Long id, GuestTabStatus status, String guestName, LocalDateTime timeOpened, LocalDateTime timeClosed, Set<OrderGroupDTO> orderGroups, double totalPrice, int localTableNumber, PaymentSummaryDTO payment) { }
