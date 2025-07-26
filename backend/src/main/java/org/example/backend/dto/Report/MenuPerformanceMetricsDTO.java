package org.example.backend.dto.Report;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MenuPerformanceMetricsDTO(
        BigDecimal totalRevenue,
        long totalItemsSold,
        long uniqueProductsSold
) {}