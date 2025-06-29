package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.example.backend.dto.GuestTab.GuestTabFilterDTO;
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

            // Use LEFT JOIN to include GuestTabs without Orders
            Join<GuestTab, Order> orderJoin = root.join("orders", JoinType.LEFT);
            Join<Order, User> userJoin = orderJoin.join("waiter", JoinType.LEFT);
            Join<Order, Product> productJoin = orderJoin.join("product", JoinType.LEFT);
            Join<GuestTab, LocalTable> localTableJoin = root.join("localTable", JoinType.LEFT);

            // Only filter out sub-orders if orderJoin is not null
            predicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(orderJoin.get("parentOrder")),
                            criteriaBuilder.isNull(orderJoin.get("id"))
                    )
            );

            if (filterDto.tableId() != null) {
                predicates.add(criteriaBuilder.equal(localTableJoin.get("id"), filterDto.tableId()));
            } else {
                // Prevents searching all tables
                predicates.add(criteriaBuilder.disjunction());
            }

            if (filterDto.guestTabIds() != null && !filterDto.guestTabIds().isEmpty()) {
                predicates.add(root.get("id").in(filterDto.guestTabIds()));
            }

            if (filterDto.orderIds() != null && !filterDto.orderIds().isEmpty()) {
                predicates.add(
                        criteriaBuilder.or(
                                orderJoin.get("id").in(filterDto.orderIds()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            if (filterDto.guestTabStatuses() != null && !filterDto.guestTabStatuses().isEmpty()) {
                predicates.add(root.get("status").in(filterDto.guestTabStatuses()));
            }

            if (filterDto.orderStatuses() != null && !filterDto.orderStatuses().isEmpty()) {
                predicates.add(
                        criteriaBuilder.or(
                                orderJoin.get("status").in(filterDto.orderStatuses()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            if (filterDto.productName() != null && !filterDto.productName().isBlank()) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")),
                                        "%" + filterDto.productName().toLowerCase() + "%"),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            if (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) {
                predicates.add(
                        criteriaBuilder.or(
                                userJoin.get("id").in(filterDto.waiterIds()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            if (filterDto.startTime() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.greaterThanOrEqualTo(orderJoin.get("orderedTime"), filterDto.startTime()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }
            if (filterDto.endTime() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.lessThanOrEqualTo(orderJoin.get("orderedTime"), filterDto.endTime()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            if (filterDto.minPrice() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.greaterThanOrEqualTo(productJoin.get("price"), filterDto.minPrice()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }
            if (filterDto.maxPrice() != null) {
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.lessThanOrEqualTo(productJoin.get("price"), filterDto.maxPrice()),
                                criteriaBuilder.isNull(orderJoin.get("id"))
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
