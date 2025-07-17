package org.example.backend.dto.IndividualPayment;

import java.math.BigDecimal;

public record IndividualPaymentDTO(Long paymentMethodId, BigDecimal amount) { }
