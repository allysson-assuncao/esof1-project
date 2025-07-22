package org.example.backend.service;

import org.example.backend.dto.Payment.RegisterPaymentRequestDTO;
import org.example.backend.model.Payment;
import org.example.backend.repository.PaymentMethodRepository;
import org.example.backend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentMethodRepository paymentMethodRepository;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService;

    @Test
    void registerIndividualPayment_WhenPaymentIdNotFound_ShouldThrowRuntimeException() {
        // config mock
        Long paymentId = -1L;
        RegisterPaymentRequestDTO paymentRequestDTO = Mockito.mock(RegisterPaymentRequestDTO.class);

        // execução e teste
        assertThrows(RuntimeException.class, () -> paymentService.registerIndividualPayments(paymentId, paymentRequestDTO));

    }

    @Test
    void registerIndividualPayment_WhenGuestTabIsPaid_ShouldThrowIllegalStateException() {
        // config mock
        Long paymentId = new Random().nextLong();
        RegisterPaymentRequestDTO paymentRequestDTO = Mockito.mock(RegisterPaymentRequestDTO.class);
    }

}
