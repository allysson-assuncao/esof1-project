package org.example.backend.dto.Category;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductSalesDTO(UUID productId, String name, double unitPrice, boolean active, long quantitySold, BigDecimal totalValue) { }
