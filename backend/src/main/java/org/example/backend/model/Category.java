package org.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"subCategories", "products"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "is_multiple", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isMultiple;

    @Column(name = "is_additional", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isAdditional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonBackReference
    private Category parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Category> subCategories;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Product> products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;

    public Category(String name) {
        this.name = name;
    }

}
