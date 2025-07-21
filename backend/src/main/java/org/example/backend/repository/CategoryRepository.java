package org.example.backend.repository;

import org.example.backend.model.Category;
import org.example.backend.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    boolean findBySubCategoriesIsEmpty();

    Optional<Category> findByName(String subCategory);

    List<Category> findByParentCategoryIsNull();

    @Query("""
        SELECT c FROM Category c WHERE c.isAdditional = true OR
        (c.isAdditional = false AND NOT EXISTS
            (SELECT s FROM Category s WHERE s.parentCategory = c AND s.isAdditional = false)
        )
    """)
    List<Category> findProductEligibleCategories();

}
