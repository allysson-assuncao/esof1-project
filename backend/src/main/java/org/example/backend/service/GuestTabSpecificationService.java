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
            Join<GuestTab, LocalTable> localTableJoin = root.join("localTable", JoinType.LEFT);

            if (filterDto.guestTabIds() != null && !filterDto.guestTabIds().isEmpty()) {
                mainPredicates.add(root.get("id").in(filterDto.guestTabIds()));
            }

            if (filterDto.guestTabStatuses() != null && !filterDto.guestTabStatuses().isEmpty()) {
                mainPredicates.add(root.get("status").in(filterDto.guestTabStatuses()));
            } else {
                mainPredicates.add(root.get("status").in(
                        GuestTabStatus.OPEN,
                        GuestTabStatus.CLOSED
                ));
            }

            /*mainPredicates.add(
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("status"), GuestTabStatus.OPEN),
                            criteriaBuilder.isNull(root.get("id"))
                    )
            );*/

            if (filterDto.tableId() != null) {
                mainPredicates.add(criteriaBuilder.equal(localTableJoin.get("id"), filterDto.tableId()));
            } else {
                // Prevents searching all tables
                mainPredicates.add(criteriaBuilder.disjunction());
            }

            if (isOrderFilterPresent(filterDto)) {
                Subquery<Long> subqueryWithFilters = query.subquery(Long.class);
                Root<Order> orderRootWithFilters = subqueryWithFilters.from(Order.class);
                List<Predicate> subQueryPredicates = new ArrayList<>();

                subQueryPredicates.add(criteriaBuilder.equal(orderRootWithFilters.get("guestTab"), root));
                subQueryPredicates.add(criteriaBuilder.isNull(orderRootWithFilters.get("parentOrder")));

                Join<Order, Product> productJoin = orderRootWithFilters.join("product");

                if (filterDto.orderIds() != null && !filterDto.orderIds().isEmpty()) {
                    subQueryPredicates.add(orderRootWithFilters.get("id").in(filterDto.orderIds()));
                }
                if (filterDto.orderStatuses() != null && !filterDto.orderStatuses().isEmpty()) {
                    subQueryPredicates.add(orderRootWithFilters.get("status").in(filterDto.orderStatuses()));
                }
                if (filterDto.waiterIds() != null && !filterDto.waiterIds().isEmpty()) {
                    subQueryPredicates.add(orderRootWithFilters.join("waiter").get("id").in(filterDto.waiterIds()));
                }
                if (filterDto.productName() != null && !filterDto.productName().isBlank()) {
                    subQueryPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("name")), "%" + filterDto.productName().toLowerCase() + "%"));
                }
                if (filterDto.startTime() != null) {
                    subQueryPredicates.add(criteriaBuilder.greaterThanOrEqualTo(orderRootWithFilters.get("orderedTime"), filterDto.startTime()));
                }
                if (filterDto.endTime() != null) {
                    subQueryPredicates.add(criteriaBuilder.lessThanOrEqualTo(orderRootWithFilters.get("orderedTime"), filterDto.endTime()));
                }
                if (filterDto.minPrice() != null) {
                    subQueryPredicates.add(criteriaBuilder.greaterThanOrEqualTo(productJoin.get("price"), filterDto.minPrice()));
                }
                if (filterDto.maxPrice() != null) {
                    subQueryPredicates.add(criteriaBuilder.lessThanOrEqualTo(productJoin.get("price"), filterDto.maxPrice()));
                }

                subqueryWithFilters.select(orderRootWithFilters.get("id")).where(criteriaBuilder.and(subQueryPredicates.toArray(new Predicate[0])));
                Predicate matchingOrdersExist = criteriaBuilder.exists(subqueryWithFilters);

                Subquery<Long> subqueryAnyOrder = query.subquery(Long.class);
                Root<Order> anyOrderRoot = subqueryAnyOrder.from(Order.class);
                subqueryAnyOrder.select(anyOrderRoot.get("id"))
                        .where(criteriaBuilder.equal(anyOrderRoot.get("guestTab"), root));
                Predicate noOrdersExist = criteriaBuilder.not(criteriaBuilder.exists(subqueryAnyOrder));

                mainPredicates.add(criteriaBuilder.or(matchingOrdersExist, noOrdersExist));
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
