package org.example.backend.dto.GuestTab;

import lombok.Builder;
import org.example.backend.dto.Order.OrderDTO;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record GuestTabDTO(Long id, String name, GuestTabStatus status, LocalDateTime timeOpened, LocalDateTime timeClosed, Set<DrillDownOrderDTO> orders, double totalPrice, int localTableNumber) { }
