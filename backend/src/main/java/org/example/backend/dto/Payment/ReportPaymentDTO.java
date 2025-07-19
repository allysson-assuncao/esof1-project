package org.example.backend.dto.Payment;

import lombok.Builder;
import org.example.backend.dto.IndividualPayment.IndividualPaymentDTO;
import org.example.backend.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReportPaymentDTO(Long id, BigDecimal totalAmount, Integer numberOfPayers, PaymentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, List<IndividualPaymentDTO> individualPayments, UUID guestTabId) { }
