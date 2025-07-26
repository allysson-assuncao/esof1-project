package org.example.backend.dto.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentGroupProjection {
    LocalDate getBusinessDate();
    BigDecimal getTotalAmount();
    long getPaymentCount();
}
