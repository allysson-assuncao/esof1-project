package org.example.backend.service;

import org.example.backend.dto.IndividualPayment.IndividualPaymentDTO;
import org.example.backend.dto.Payment.PaymentGroupDTO;
import org.example.backend.dto.Payment.ReportPaymentDTO;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.model.Payment;
import org.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final PaymentSpecificationService paymentSpecificationService;

    @Autowired
    public ReportService(PaymentRepository paymentRepository, PaymentSpecificationService paymentSpecificationService) {
        this.paymentRepository = paymentRepository;
        this.paymentSpecificationService = paymentSpecificationService;
    }

    public Page<PaymentGroupDTO> getGroupedPaymentsByFilters(GeneralReportFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        Specification<Payment> specification = this.paymentSpecificationService.getAPIProcessSpecification(filterDto);
        List<Payment> allPayments = this.paymentRepository.findAll(specification);

        LocalTime businessDayStart = Optional.ofNullable(filterDto.businessDayStartTime()).orElse(LocalTime.of(18, 0)); // Default 18:00

        Map<LocalDate, List<Payment>> groupedByBusinessDay = allPayments.stream()
                .collect(Collectors.groupingBy(payment -> {
                    LocalDateTime createdAt = payment.getCreatedAt();
                    if (createdAt.toLocalTime().isBefore(businessDayStart)) {
                        return createdAt.toLocalDate().minusDays(1);
                    }
                    return createdAt.toLocalDate();
                }));

        List<PaymentGroupDTO> paymentGroups = groupedByBusinessDay.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Payment> paymentsInGroup = entry.getValue();

                    BigDecimal totalAmount = paymentsInGroup.stream()
                            .map(Payment::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    List<ReportPaymentDTO> reportPaymentDTOs = paymentsInGroup.stream()
                            .map(this::mapPaymentToReportDTO)
                            .collect(Collectors.toList());

                    return PaymentGroupDTO.builder()
                            .date(date)
                            .payments(reportPaymentDTOs)
                            .paymentCount(paymentsInGroup.size())
                            .totalAmount(totalAmount)
                            .build();
                })
                .sorted(Comparator.comparing(PaymentGroupDTO::date).reversed())
                .collect(Collectors.toList());

        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min((start + size), paymentGroups.size());
        List<PaymentGroupDTO> paginatedContent = paymentGroups.subList(start, end);

        return new PageImpl<>(paginatedContent, PageRequest.of(page, size), paymentGroups.size());
    }

    private ReportPaymentDTO mapPaymentToReportDTO(Payment payment) {
        List<IndividualPaymentDTO> individualDTOs = payment.getIndividualPayments().stream()
                .map(ip -> IndividualPaymentDTO.builder()
                        .id(ip.getId())
                        .amount(ip.getAmount())
                        .paymentMethodName(ip.getPaymentMethod().getName())
                        .build())
                .collect(Collectors.toList());

        return ReportPaymentDTO.builder()
                .id(payment.getId())
                .totalAmount(payment.getTotalAmount())
                .numberOfPayers(payment.getNumberOfPayers())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .guestTabId(payment.getGuestTab().getId())
                .individualPayments(individualDTOs)
                .build();
    }

}
