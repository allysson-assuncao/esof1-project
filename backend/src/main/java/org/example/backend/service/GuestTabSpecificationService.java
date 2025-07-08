package org.example.backend.service;

import jakarta.persistence.criteria.*;
import org.example.backend.dto.GuestTab.GuestTabFilterDTO;
import org.example.backend.model.*;
import org.example.backend.model.Order;
import org.example.backend.model.enums.GuestTabStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuestTabSpecificationService {

    public Specification<GuestTab> getGuestTabSpecification(GuestTabFilterDTO filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> mainPredicates = new ArrayList<>();

            assert query != null;
            query.distinct(true);

            // Use LEFT JOIN to include GuestTabs without Orders
            Join<GuestTab, Order> orderJoin = root.join("orders", JoinType.LEFT);
            Join<Order, User> userJoin = orderJoin.join("waiter", JoinType.LEFT);
            Join<GuestTab, LocalTable> localTableJoin = root.join("localTable", JoinType.LEFT);

            if (filterDto.guestTabIds() != null && !filterDto.guestTabIds().isEmpty()) {
                mainPredicates.add(root.get("id").in(filterDto.guestTabIds()));
            }

            /*if (filterDto.guestTabStatuses() != null && !filterDto.guestTabStatuses().isEmpty()) {
                mainPredicates.add(root.get("status").in(filterDto.guestTabStatuses()));
            }*/

            mainPredicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("status"), GuestTabStatus.OPEN),
                            criteriaBuilder.isNull(root.get("id"))
                    )
            );

            if (filterDto.tableId() != null) {
                mainPredicates.add(criteriaBuilder.equal(localTableJoin.get("id"), filterDto.tableId()));
            } else {
                // Prevents searching all tables
                mainPredicates.add(criteriaBuilder.disjunction());
            }

            List<Predicate> subQueryPredicates = new ArrayList<>();

            if (isOrderFilterPresent(filterDto)) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Order> subOrderRoot = subquery.from(Order.class);
                subQueryPredicates.add(criteriaBuilder.equal(subOrderRoot.get("guestTab"), root));

                // Only filter out sub-orders if orderJoin is null
                subQueryPredicates.add(criteriaBuilder.isNull(subOrderRoot.get("parentOrder")));

                if (filterDto.orderIds() != null && !filterDto.orderIds().isEmpty()) {
                    subQueryPredicates.add(subOrderRoot.get("id").in(filterDto.orderIds()));
                }

                if (filterDto.orderStatuses() != null && !filterDto.orderStatuses().isEmpty()) {
                    subQueryPredicates.add(subOrderRoot.get("status").in(filterDto.orderStatuses()));
                }

                if (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) {
                    subQueryPredicates.add(subOrderRoot.join("waiter").get("id").in(filterDto.waiterIds()));
                }

                if (filterDto.productName() != null && !filterDto.productName().isBlank()) {
                    Join<Order, Product> productJoin = subOrderRoot.join("product");
                    subQueryPredicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(productJoin.get("name")),
                            "%" + filterDto.productName().toLowerCase() + "%"
                    ));
                }

                if (filterDto.startTime() != null) {
                    subQueryPredicates.add(criteriaBuilder.greaterThanOrEqualTo(subOrderRoot.get("orderedTime"), filterDto.startTime()));
                }

                if (filterDto.endTime() != null) {
                    subQueryPredicates.add(criteriaBuilder.lessThanOrEqualTo(subOrderRoot.get("orderedTime"), filterDto.endTime()));
                }

                Join<Order, Product> productPriceJoin = subOrderRoot.join("product");
                if (filterDto.minPrice() != null) {
                    subQueryPredicates.add(criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), filterDto.minPrice()));
                }
                if (filterDto.maxPrice() != null) {
                    subQueryPredicates.add(criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), filterDto.maxPrice()));
                }

                subquery.select(subOrderRoot.get("id"))
                        .where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));

                mainPredicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(mainPredicates.toArray(new Predicate[0]));
        };
    }

    private boolean isOrderFilterPresent(GuestTabFilterDTO filterDto) {
        return (filterDto.orderIds() != null && !filterDto.orderIds().isEmpty()) ||
                (filterDto.orderStatuses() != null && !filterDto.orderStatuses().isEmpty()) ||
                (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) ||
                (filterDto.productName() != null && !filterDto.productName().isBlank()) ||
                filterDto.startTime() != null ||
                filterDto.endTime() != null ||
                filterDto.minPrice() != null ||
                filterDto.maxPrice() != null;
    }

}
