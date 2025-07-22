package org.example.backend.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.example.backend.dto.Report.MenuReportFilterDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Order;
import org.example.backend.model.Product;
import org.example.backend.model.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuReportSpecificationService {

    public Specification<Order> getSpecification(MenuReportFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Order, Product> productJoin = root.join("product", JoinType.INNER);

            Optional.ofNullable(filter.startDate())
                    .ifPresent(start -> predicates.add(cb.greaterThanOrEqualTo(root.get("orderedTime"), start)));
            Optional.ofNullable(filter.endDate())
                    .ifPresent(end -> predicates.add(cb.lessThanOrEqualTo(root.get("orderedTime"), end)));

            Optional.ofNullable(filter.minPrice())
                    .ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(productJoin.get("price"), min)));
            Optional.ofNullable(filter.maxPrice())
                    .ifPresent(max -> predicates.add(cb.lessThanOrEqualTo(productJoin.get("price"), max)));

            if (filter.productIds() != null && !filter.productIds().isEmpty()) {
                predicates.add(productJoin.get("id").in(filter.productIds()));
            }

            if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
                Join<Product, Category> categoryJoin = productJoin.join("category", JoinType.INNER);
                predicates.add(categoryJoin.get("id").in(filter.categoryIds()));
            }

            predicates.add(cb.notEqual(root.get("status"), OrderStatus.CANCELED));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
