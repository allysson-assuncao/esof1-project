package org.example.backend.dto.Payment;

import lombok.Builder;
import org.example.backend.model.enums.PaymentStatus;

import java.math.BigDecimal;

@Builder
public record PaymentSummaryDTO(Long id, BigDecimal totalAmount, PaymentStatus status) {}
