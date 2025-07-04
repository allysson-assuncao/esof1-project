package org.example.backend.dto.Order;

import java.util.UUID;

public record RegisterOrderDTO(int amount, String observation, UUID productId) { }
