package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Set;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "observation", nullable = false)
    private String observation;

    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_time", nullable = false)
    private LocalDateTime orderedTime;

    @Column(name = "preparation_time", nullable = false)
    private LocalDateTime preparationTime;

    @Column(name = "ready_time", nullable = false)
    private LocalDateTime readyTime;

    @Column(name = "closed_time", nullable = false)
    private LocalDateTime closedTime; // delivered or canceled, this treats both

    @OneToMany(mappedBy = "parentOrder", cascade = CascadeType.ALL)
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
