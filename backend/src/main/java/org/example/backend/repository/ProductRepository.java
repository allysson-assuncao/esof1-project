package org.example.backend.repository;

import org.example.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findById(UUID uuid);
    boolean existsByName(String name);

    Optional<Product> findByName(String name);
    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByCategoryName(String categoryName);

}
