package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
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
                            criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTime)
                    ));

            Optional.ofNullable(filterDto.endDate())
                    .ifPresent(endTime -> predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime)
                    ));



            assert query != null;
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
