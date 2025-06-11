package org.example.backend.dto;

import java.util.UUID;

public record OrderRequestDTO(int amount, String observation, Long parentOrderId, Long guestTabId, UUID productId, String userEmail) {
}
