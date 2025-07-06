package org.example.backend.repository;

import org.example.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Product> findByCategory_SubCategoriesIsEmptyAndOrdersIsNotEmpty();

    List<Product> findByCategory_SubCategoriesIsNotEmptyAndOrdersIsNotEmpty();

    @Query("""
            SELECT p FROM Product p
            WHERE p.category.id IN (
                SELECT subCat.id FROM Order o
                JOIN o.product prod
                JOIN prod.category cat
                JOIN cat.subCategories subCat
                WHERE o.id = :parentOrderId
            )
            OR p.category.id = (
                SELECT prod.category.id FROM Order o
                JOIN o.product prod
                WHERE o.id = :parentOrderId
            )
            """)
    List<Product> findProductsFromParentOrderCategoryAndSubcategories(@Param("parentOrderId") Long parentOrderId);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.subCategories IS NOT EMPTY")
    List<Product> findProductsWithCategoryWithSubcategories();

}
