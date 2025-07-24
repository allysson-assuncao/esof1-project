package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.Order.OrderKanbanFilterDTO;
import org.example.backend.model.*;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderSpecificationService {

    public Specification<Order> getOrderKanbanSpecification(OrderKanbanFilterDTO filterDto, OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isNull(root.get("parentOrder")));

            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            if (filterDto.workstationIds() != null && !filterDto.workstationIds().isEmpty()) {
                Join<Order, Workstation> workstationJoin = root.join("workstation", JoinType.LEFT);
                predicates.add(workstationJoin.get("id").in(filterDto.workstationIds()));
            }

            Optional.ofNullable(filterDto.startTime())
                    .ifPresent(startTime -> predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("orderedTime"), startTime)
                    ));

            Optional.ofNullable(filterDto.endTime())
                    .ifPresent(endTime -> predicates.add(
                            criteriaBuilder.lessThanOrEqualTo(root.get("orderedTime"), endTime)
                    ));

            assert query != null;
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
