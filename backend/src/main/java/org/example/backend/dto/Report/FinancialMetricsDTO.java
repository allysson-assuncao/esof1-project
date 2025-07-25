package org.example.backend.dto.Report;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinancialMetricsDTO(
   BigDecimal totalRevenue,
   Long totalPayments,
   BigDecimal averageTicket
) {}
