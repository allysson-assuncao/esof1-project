package org.example.backend.dto.Payment;

import org.example.backend.dto.IndividualPayment.SimpleIndividualPaymentDTO;

import java.util.List;

public record RegisterPaymentRequestDTO(List<SimpleIndividualPaymentDTO> individualPayments) { }
