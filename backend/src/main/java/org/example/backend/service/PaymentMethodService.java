package org.example.backend.service;

import org.example.backend.dto.PaymentMethod.SimplePaymentMethodDTO;
import org.example.backend.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public List<SimplePaymentMethodDTO> findAll() {
        return paymentMethodRepository.findAll().stream()
                .map(pm -> new SimplePaymentMethodDTO(pm.getId(), pm.getName()))
                .collect(Collectors.toList());
    }

}
