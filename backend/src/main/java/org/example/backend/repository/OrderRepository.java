package org.example.backend.repository;

import org.example.backend.dto.Order.FlatOrderDTO;
import org.example.backend.dto.Report.ProductSalesProjection;
import org.example.backend.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByGuestTabLocalTableId(UUID localTableId);

    List<Order> findByGuestTabId(Long guestTabId);

    List<Order> findByParentOrderId(Long id);

    Optional<Order> findById(Long id);
    /*Optional<List<Order>> findByStatusAndProduct_Destination(OrderStatus status, ProductDestination destination);*/

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.product " +
            "JOIN FETCH o.waiter " +
            "LEFT JOIN FETCH o.additionalOrders " +
            "WHERE o.guestTab.id IN :guestTabIds AND o.parentOrder IS NULL")
    List<Order> findTopLevelOrdersWithAdditionalsByGuestTabIds(@Param("guestTabIds") List<Long> guestTabIds);

    @Query("""
                SELECT new org.example.backend.dto.Order.FlatOrderDTO(
                    o.id,
                    o.amount,
                    o.observation,
                    o.status,
                    o.orderedTime,
                    p_ord.id,
                    gt.id,
                    p.name,
                    p.price,
                    w.name
                )
                FROM Order o
                JOIN o.guestTab gt
                JOIN o.product p
                JOIN o.waiter w
                LEFT JOIN o.parentOrder p_ord
                WHERE gt.id IN :guestTabIds
            """)
    List<FlatOrderDTO> findFlatOrderDTOsByGuestTabIds(@Param("guestTabIds") List<Long> guestTabIds);

    @Override
    @EntityGraph(value = "Order.withAdditionalOrders")
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query(
            value = """
                    SELECT
                        p.id as productId,
                        p.name as name,
                        p.price as unitPrice,
                        p.active as active,
                        CAST(COUNT(o.id) AS bigint) as quantitySold,
                        SUM(p.price) as totalValue,
                        p.category_id as categoryId
                    FROM
                        orders o
                    INNER JOIN
                        products p ON o.product_id = p.id
                    WHERE
                        (CAST(:startDate AS timestamp) IS NULL OR o.ordered_time >= :startDate) AND
                        (CAST(:endDate AS timestamp) IS NULL OR o.ordered_time <= :endDate) AND
                        (:productIds IS NULL OR p.id IN (:productIds)) AND
                        (:categoryIds IS NULL OR p.category_id IN (:categoryIds)) AND
                        (CAST(:minPrice AS numeric) IS NULL OR p.price >= :minPrice) AND
                        (CAST(:maxPrice AS numeric) IS NULL OR p.price <= :maxPrice) AND
                        o.status <> 4
                    GROUP BY
                        p.id, p.name, p.price, p.active, p.category_id
                    """,
            nativeQuery = true
    )
    List<ProductSalesProjection> getAggregatedMenuSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("productIds") Set<UUID> productIds,
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

}
