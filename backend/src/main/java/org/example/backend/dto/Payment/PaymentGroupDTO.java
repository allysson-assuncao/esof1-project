package org.example.backend.dto.Payment;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record PaymentGroupDTO(LocalDate date, BigDecimal totalAmount, Integer paymentCount, List<ReportPaymentDTO> payments) { }
