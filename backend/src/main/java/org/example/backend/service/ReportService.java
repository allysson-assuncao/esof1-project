package org.example.backend.service;

import org.example.backend.dto.IndividualPayment.IndividualPaymentDTO;
import org.example.backend.dto.Payment.PaymentGroupDTO;
import org.example.backend.dto.Payment.PaymentGroupProjection;
import org.example.backend.dto.Payment.ReportPaymentDTO;
import org.example.backend.dto.Report.FinancialMetricsDTO;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.model.Payment;
import org.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public ReportService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Page<PaymentGroupDTO> getGroupedPaymentsByFilters(GeneralReportFilterDTO filterDto, int page, int size, String orderBy, Sort.Direction direction) {
        LocalTime businessDayStart = Optional.ofNullable(filterDto.businessDayStartTime()).orElse(LocalTime.of(18, 0));
        LocalTime businessDayEnd = Optional.ofNullable(filterDto.businessDayEndTime()).orElse(LocalTime.of(2, 0));

        LocalDateTime queryStartDate = null;
        if (filterDto.startDate() != null) {
            queryStartDate = filterDto.startDate().toLocalDate().atTime(businessDayStart);
        }

        LocalDateTime queryEndDate = null;
        if (filterDto.endDate() != null) {
            queryEndDate = filterDto.endDate().toLocalDate().atTime(businessDayEnd);
            if (businessDayEnd.isBefore(businessDayStart)) {
                queryEndDate = queryEndDate.plusDays(1);
            }
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, "businessDay"));

        Page<PaymentGroupProjection> groupedPage = this.paymentRepository.findGroupedPayments(
                queryStartDate,
                queryEndDate,
                businessDayStart.toString(),
                pageable
        );

        List<PaymentGroupProjection> projections = groupedPage.getContent();

        if (projections.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<LocalDate> businessDaysOnPage = projections.stream()
                .map(PaymentGroupProjection::getBusinessDay)
                .collect(Collectors.toList());

        List<Payment> paymentsForPage = this.paymentRepository.findPaymentsByBusinessDays(
                businessDaysOnPage,
                queryStartDate,
                queryEndDate,
                businessDayStart
        );

        Map<LocalDate, List<Payment>> paymentsByDateMap = paymentsForPage.stream()
                .collect(Collectors.groupingBy(payment -> {
                    LocalDateTime createdAt = payment.getCreatedAt();
                    if (createdAt.toLocalTime().isBefore(businessDayStart)) {
                        return createdAt.toLocalDate().minusDays(1);
                    }
                    return createdAt.toLocalDate();
                }));

        List<PaymentGroupDTO> finalResult = projections.stream()
                .map(projection -> {
                    LocalDate businessDay = projection.getBusinessDay();
                    List<Payment> paymentsInGroup = paymentsByDateMap.getOrDefault(businessDay, Collections.emptyList());

                    List<ReportPaymentDTO> reportPaymentDTOs = paymentsInGroup.stream()
                            .map(this::mapPaymentToReportDTO)
                            .collect(Collectors.toList());

                    return new PaymentGroupDTO(
                            businessDay,
                            projection.getTotalAmount(),
                            projection.getPaymentCount(),
                            reportPaymentDTOs
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(finalResult, groupedPage.getPageable(), groupedPage.getTotalElements());
    }

    @Transactional
    public FinancialMetricsDTO getFinancialMetrics(GeneralReportFilterDTO filterDto) {

        Specification<Payment> specification = this.paymentSpecificationService.getAPIProcessSpecification(filterDto);
        List<Payment> filteredPayments = this.paymentRepository.findAll(specification);

        if(filteredPayments.isEmpty()) {
            return FinancialMetricsDTO.builder()
                    .averageTicket(BigDecimal.ZERO)
                    .totalPayments(0L)
                    .totalRevenue(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal totalRevenue = filteredPayments.stream()
                .map(payment -> payment.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Long totalPayments = (long) filteredPayments.size();

        BigDecimal averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalPayments), 2, RoundingMode.HALF_UP);


        return FinancialMetricsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalPayments(totalPayments)
                .averageTicket(averageTicket)
                .build();
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
