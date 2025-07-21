package org.example.backend.service;

import org.example.backend.dto.IndividualPayment.SimpleIndividualPaymentDTO;
import org.example.backend.dto.Payment.RegisterPaymentRequestDTO;
import org.example.backend.model.GuestTab;
import org.example.backend.model.IndividualPayment;
import org.example.backend.model.Payment;
import org.example.backend.model.PaymentMethod;
import org.example.backend.model.enums.GuestTabStatus;
import org.example.backend.model.enums.PaymentStatus;
import org.example.backend.repository.GuestTabRepository;
import org.example.backend.repository.IndividualPaymentRepository;
import org.example.backend.repository.PaymentMethodRepository;
import org.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final IndividualPaymentRepository individualPaymentRepository;
    private final GuestTabRepository guestTabRepository;
    private final LocalTableService localTableService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, PaymentMethodRepository paymentMethodRepository, IndividualPaymentRepository individualPaymentRepository, GuestTabRepository guestTabRepository, LocalTableService localTableService) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.individualPaymentRepository = individualPaymentRepository;
        this.guestTabRepository = guestTabRepository;
        this.localTableService = localTableService;
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

    @Transactional
    public Payment registerIndividualPayments(Long paymentId, RegisterPaymentRequestDTO request) {
        Payment payment = this.paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado com id: " + paymentId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Este pagamento já foi totalmente quitado.");
        }

        for (SimpleIndividualPaymentDTO dto : request.individualPayments()) {
            PaymentMethod method = this.paymentMethodRepository.findById(dto.paymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Método de pagamento não encontrado."));

            IndividualPayment individualPayment = IndividualPayment.builder()
                    .payment(payment)
                    .paymentMethod(method)
                    .amount(dto.amount())
                    .build();
            this.individualPaymentRepository.save(individualPayment);
        }

        Payment updatedPayment = paymentRepository.findById(paymentId).get();

        BigDecimal totalPaid = updatedPayment.getIndividualPayments().stream()
                .map(IndividualPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(updatedPayment.getTotalAmount()) > 0) {
            throw new IllegalStateException("O valor pago não pode exceder o valor total da comanda.");
        }

        if (totalPaid.compareTo(updatedPayment.getTotalAmount()) == 0) {
            updatedPayment.setStatus(PaymentStatus.PAID);

            GuestTab guestTab = updatedPayment.getGuestTab();
            guestTab.setStatus(GuestTabStatus.PAYED);
            this.guestTabRepository.save(guestTab);

            if (guestTab.getLocalTable() != null) {
                this.localTableService.updateTableStatusBasedOnGuestTabs(guestTab.getLocalTable().getId());
            }

        } else {
            updatedPayment.setStatus(PaymentStatus.PARTIALLY_PAID);
        }

        updatedPayment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(updatedPayment);
    }

}
