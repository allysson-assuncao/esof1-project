package org.example.backend.controller;

import org.example.backend.dto.Payment.RegisterPaymentRequestDTO;
import org.example.backend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/app/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{paymentId}/register")
    public ResponseEntity<Void> registerPayment(
            @PathVariable Long paymentId,
            @RequestBody RegisterPaymentRequestDTO request) {
        paymentService.registerIndividualPayments(paymentId, request);
        return ResponseEntity.ok().build();
    }

}
