package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.OrderStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.Set;

@Table(name = "orders")
@NamedEntityGraph(
        name = "Order.withAdditionalOrders",
        attributeNodes = {
                @NamedAttributeNode("additionalOrders")
        }
)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "observation", nullable = false)
    private String observation;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_time")
    private LocalDateTime orderedTime;

    @Column(name = "preparation_time")
    private LocalDateTime preparationTime;

    @Column(name = "ready_time")
    private LocalDateTime readyTime;

    @Column(name = "closed_time")
    private LocalDateTime closedTime; // delivered or canceled, this treats both

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Order> additionalOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_order_id")
    private Order parentOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guestTab_id", nullable = false)
    private GuestTab guestTab;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User waiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;

}
