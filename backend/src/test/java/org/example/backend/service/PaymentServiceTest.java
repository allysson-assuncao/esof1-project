package org.example.backend.service;

import org.example.backend.dto.IndividualPayment.SimpleIndividualPaymentDTO;
import org.example.backend.dto.Payment.RegisterPaymentRequestDTO;
import org.example.backend.model.*;
import org.example.backend.model.enums.PaymentStatus;
import org.example.backend.repository.PaymentMethodRepository;
import org.example.backend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(Payment.builder()
                .status(PaymentStatus.PAID).build()));

        // execução e teste
        assertThrows(IllegalStateException.class, () -> paymentService.registerIndividualPayments(paymentId, paymentRequestDTO));
    }

    @Test
    void registerIndividualPayment_WhenValueIsGreaterThanTabValue_ShouldThrowIllegalStateException() {
        // config mock
        Long paymentId = new Random().nextLong();
        List<SimpleIndividualPaymentDTO> individualPayments = new ArrayList<>();
        individualPayments.add(SimpleIndividualPaymentDTO.builder().amount(BigDecimal.valueOf(212.00)).build());
        RegisterPaymentRequestDTO paymentRequestDTO = new RegisterPaymentRequestDTO(individualPayments);
        Product product = Product.builder().price(20.00).build();
        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder().amount(2).product(product).build());
        GuestTab guestTab = GuestTab.builder().orders(orders).build();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(Payment.builder().status(PaymentStatus.PENDING).totalAmount(BigDecimal.valueOf(20.00))
                .guestTab(guestTab).build()));

        // execução e teste
        assertThrows(IllegalStateException.class, () -> paymentService.registerIndividualPayments(paymentId,paymentRequestDTO));
    }

}
