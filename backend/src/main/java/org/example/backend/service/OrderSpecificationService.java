package org.example.backend.service;

import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.GuestTab.GuestTabFilterDTO;
import org.example.backend.model.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderSpecificationService {

    public Specification<Order> getOrderSpecification(GuestTabFilterDTO filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
