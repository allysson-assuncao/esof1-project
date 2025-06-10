package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record GuestTabDTO(Long id, String name, GuestTabStatus status, LocalDateTime timeOpened, LocalDateTime timeClosed, Set<OrderDTO> orders, double totalPrice, int localTableNumber) { }
