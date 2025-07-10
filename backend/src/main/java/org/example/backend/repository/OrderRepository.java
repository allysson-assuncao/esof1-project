package org.example.backend.repository;

import org.example.backend.dto.Order.FlatOrderDTO;
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

import java.util.List;
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

}
