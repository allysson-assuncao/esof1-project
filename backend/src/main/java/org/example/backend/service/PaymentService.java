package org.example.backend.service;

import org.example.backend.model.GuestTab;
import org.example.backend.model.Payment;
import org.example.backend.model.enums.PaymentStatus;
import org.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPendingPaymentForGuestTab(GuestTab guestTab, int numberOfPayers) {
        BigDecimal totalAmount = guestTab.getOrders().stream()
                .filter(order -> order.getParentOrder() == null)
                .map(order -> BigDecimal.valueOf(order.getProduct().getPrice() * order.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = Payment.builder()
                .guestTab(guestTab)
                .totalAmount(totalAmount)
                .numberOfPayers(numberOfPayers)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

}
