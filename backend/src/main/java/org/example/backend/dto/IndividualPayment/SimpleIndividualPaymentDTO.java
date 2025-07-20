package org.example.backend.dto.IndividualPayment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SimpleIndividualPaymentDTO(Long paymentMethodId, BigDecimal amount) { }
