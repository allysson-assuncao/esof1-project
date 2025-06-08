package org.example.backend.repository;

import org.example.backend.model.LocalTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalTableRepository extends JpaRepository<LocalTable, UUID> {
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO tables (number, status) VALUES (:number, 0)")


    void insertLocalTable(@Param("number") int number);
    Optional<LocalTable> findById(UUID id);
    Optional<LocalTable> findByNumber(int number);

}
