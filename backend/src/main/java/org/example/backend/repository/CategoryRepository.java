package org.example.backend.repository;

import org.example.backend.model.Category;
import org.example.backend.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    boolean findBySubCategoriesIsEmpty();

    Optional<Category> findByName(String subCategory);
}
