package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;

@Builder
public record GuestTabDTO(Long guestTabId, GuestTabStatus guestTabStatus, LocalDateTime guestTabTimeOpened, double totalPrice, int localTableNumber) { }
