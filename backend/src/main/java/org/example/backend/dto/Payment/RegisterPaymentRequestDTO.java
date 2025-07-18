package org.example.backend.dto.Payment;

import org.example.backend.dto.IndividualPayment.IndividualPaymentDTO;

import java.util.List;

public record RegisterPaymentRequestDTO(List<IndividualPaymentDTO> individualPayments) { }
