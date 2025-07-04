package org.example.backend.repository;

import org.example.backend.dto.Order.FlatOrderDTO;
import org.example.backend.model.Order;
import org.example.backend.model.enums.OrderStatus;
/*import org.example.backend.model.enums.ProductDestination;*/
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

    Optional<Order> findById(Long id);
    /*Optional<List<Order>> findByStatusAndProduct_Destination(OrderStatus status, ProductDestination destination);*/

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

}
