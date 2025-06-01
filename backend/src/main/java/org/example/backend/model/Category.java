package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Table(name = "categories")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sub_categories", nullable = false)
    private Set<Category> subCategories;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Product> products;


}
