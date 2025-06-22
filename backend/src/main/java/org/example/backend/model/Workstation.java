package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table(name = "workstation")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"users", "ordersQueue", "categories"})
public class Workstation {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "workstation_user",
        joinColumns = @JoinColumn(name = "workstation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    @OneToMany(mappedBy = "workstation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> ordersQueue;

    @OneToMany(mappedBy = "workstation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Category> categories;


}
