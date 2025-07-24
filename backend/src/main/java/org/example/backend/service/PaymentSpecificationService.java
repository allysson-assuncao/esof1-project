package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.model.IndividualPayment;
import org.example.backend.model.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentSpecificationService {

    public Specification<Payment> getAPIProcessSpecification(GeneralReportFilterDTO filterDto) {
    return (root, query, criteriaBuilder) -> {
        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(filterDto.startDate())
                .ifPresent(startTime -> predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime)
                ));

        Optional.ofNullable(filterDto.endDate())
                .ifPresent(endTime -> predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime)
                ));

        if (filterDto.paymentMethodIds() != null && !filterDto.paymentMethodIds().isEmpty()) {
            Join<Payment, IndividualPayment> individualPaymentJoin = root.join("individualPayments");
            predicates.add(individualPaymentJoin.get("paymentMethod").get("id").in(filterDto.paymentMethodIds()));
        }

        assert query != null;
        query.distinct(true);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
}

}
