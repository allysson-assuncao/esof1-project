package org.example.backend.dto.Order;

import java.util.UUID;

public record OrderItemDTO(int amount, String observation, UUID productId) { }
