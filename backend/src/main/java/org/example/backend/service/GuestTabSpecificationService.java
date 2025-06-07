package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.GuestTabFilterDTO;
import org.example.backend.model.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuestTabSpecificationService {

    public Specification<GuestTab> getGuestTabSpecification(GuestTabFilterDTO filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Order, User> userJoin = root.join("waiter");
            Join<Order, Product> productJoin = root.join("product");
            Join<Order, GuestTab> guestTabJoin = root.join("guestTab");
            Join<GuestTab, LocalTable> localTableJoin = guestTabJoin.join("localTable");

            /*if (filterDto.tableId() != null) {
                predicates.add(criteriaBuilder.equal(localTableJoin.get("id"), filterDto.tableId()));
            } else {
                // Maybe throw an exception
                // Prevents searching all tables
                predicates.add(criteriaBuilder.disjunction());
            }

            if (filterDto.guestTabIds() != null && !filterDto.guestTabIds().isEmpty()) {
                predicates.add(guestTabJoin.get("id").in(filterDto.guestTabIds()));
            }

            if (filterDto.orderIds() != null && !filterDto.orderIds().isEmpty()) {
                predicates.add(root.get("id").in(filterDto.orderIds()));
            }

            if (filterDto.orderStatuses() != null && !filterDto.orderStatuses().isEmpty()) {
                 predicates.add(root.get("status").in(filterDto.orderStatuses()));
            }

            if (filterDto.guestTabStatuses() != null && !filterDto.guestTabStatuses().isEmpty()) {
                 predicates.add(guestTabJoin.get("status").in(filterDto.guestTabStatuses()));
            }*/

            if (filterDto.productName() != null && !filterDto.productName().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")),
                        "%" + filterDto.productName().toLowerCase() + "%"));
            }

            /*if (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) {
                predicates.add(userJoin.get("id").in(filterDto.waiterIds()));
            }*/

            if (filterDto.startTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderedTime"), filterDto.startTime()));
            }
            if (filterDto.endTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderedTime"), filterDto.endTime()));
            }

            if (filterDto.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(productJoin.get("price"), filterDto.minPrice()));
            }
            if (filterDto.maxPrice() != null) {
                 predicates.add(criteriaBuilder.lessThanOrEqualTo(productJoin.get("price"), filterDto.maxPrice()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
