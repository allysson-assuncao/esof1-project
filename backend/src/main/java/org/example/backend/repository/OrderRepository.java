package org.example.backend.repository;

import org.example.backend.model.Order;
import org.example.backend.model.enums.OrderStatus;
/*import org.example.backend.model.enums.ProductDestination;*/
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

}
