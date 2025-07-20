package org.example.backend.dto.IndividualPayment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record IndividualPaymentDTO(Long id, BigDecimal amount, String paymentMethodName) { }
