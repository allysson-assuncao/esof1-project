package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.OrderFilterDTO;
import org.example.backend.model.Order;
import org.example.backend.model.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderSpecificationService {

    public Specification<Order> getOrderSpecification(OrderFilterDTO filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Order, Product> productJoin = root.join("product");

            Optional.ofNullable(filterDto.productName())
                    .ifPresent(name -> {
                        predicates.add(criteriaBuilder.like(productJoin.get("name"), "%" + name + "%"));
                    });

            if (filterDto.status() != null && !filterDto.status().isEmpty()) {
                predicates.add(root.get("status").in(filterDto.status()));
            }

            Optional.ofNullable(filterDto.orderedTime())
                    .ifPresent(orderedTime -> predicates.add(
                            criteriaBuilder.greaterThanOrEqualTo(root.get("orderedTime"), orderedTime)
                    ));

            if (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) {
                predicates.add(root.get("waiter").in(filterDto.waiterIds()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
