package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.model.IndividualPayment;
import org.example.backend.model.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentSpecificationService {

    public Specification<Payment> getAPIProcessSpecification(GeneralReportFilterDTO filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            LocalTime businessDayStart = Optional.ofNullable(filterDto.businessDayStartTime()).orElse(LocalTime.of(18, 0));
            LocalTime businessDayEnd = Optional.ofNullable(filterDto.businessDayEndTime()).orElse(LocalTime.of(2, 0));

            Optional.ofNullable(filterDto.startDate()).ifPresent(start -> {
                LocalDateTime queryStartDate = start.toLocalDate().atTime(businessDayStart);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), queryStartDate));
            });

            Optional.ofNullable(filterDto.endDate()).ifPresent(end -> {
                LocalDateTime queryEndDate = end.toLocalDate().atTime(businessDayEnd);
                if (businessDayEnd.isBefore(businessDayStart)) {
                    queryEndDate = queryEndDate.plusDays(1);
                }
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), queryEndDate));
            });

            if (filterDto.paymentMethodIds() != null && !filterDto.paymentMethodIds().isEmpty()) {
                Join<Payment, IndividualPayment> individualPaymentJoin = root.join("individualPayments", JoinType.LEFT);
                predicates.add(individualPaymentJoin.get("paymentMethod").get("id").in(filterDto.paymentMethodIds()));
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
