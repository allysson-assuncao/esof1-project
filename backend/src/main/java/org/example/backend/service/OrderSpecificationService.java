package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.GuestTab.GuestTabFilterDTO;
import org.example.backend.dto.Order.OrderKanbanFilterDTO;
import org.example.backend.model.*;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderSpecificationService {

    public Specification<Order> getOrderKanbanSpecification(OrderKanbanFilterDTO filterDto, OrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Order, Workstation> workstationJoin = root.join("workstation");

            predicates.add(criteriaBuilder.isNull(root.get("parentOrder")));

            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            if (filterDto.workstationIds() != null && !filterDto.workstationIds().isEmpty()) {
                predicates.add(workstationJoin.get("id").in(filterDto.workstationIds()));
            }

            assert query != null;
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
