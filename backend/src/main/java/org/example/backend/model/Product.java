package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Table(name = "products")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "additional_list", nullable = false)
    private Set<Category> additionalList;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "categories_id")
    )
    @JsonIgnore
    private Set<Category> categoryList;

    @ManyToMany(mappedBy = "guest_tabs", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<GuestTab> guestTabs;

}
