package org.example.backend.service;

import org.example.backend.dto.Payment.ReportPaymentDTO;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.model.Payment;
import org.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final PaymentSpecificationService paymentSpecificationService;

    @Autowired
    public ReportService(PaymentRepository paymentRepository, PaymentSpecificationService paymentSpecificationService) {
        this.paymentRepository = paymentRepository;
        this.paymentSpecificationService = paymentSpecificationService;
    }


    public Page<ReportPaymentDTO> getPaymentsByFilters(GeneralReportFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<Payment> specification = this.paymentSpecificationService.getAPIProcessSpecification(filterDto);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, orderBy));
        Page<Payment> paymentsPage = this.paymentRepository.findAll(specification, pageable);

        return paymentsPage.map(payment -> ReportPaymentDTO.builder()
                .id(payment.getId())
                .totalAmount(payment.getTotalAmount())
                .numberOfPayers(payment.getNumberOfPayers())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build());
    }
}
