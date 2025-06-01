package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Table(name = "orders")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "observation", nullable = false)
    private String observation;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_time")
    private LocalDateTime orderedTime;

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL)
    private Set<Order> additionalOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_order_id")
    private Order parentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestTab_id")
    private GuestTab guestTab;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User waiter;

}
