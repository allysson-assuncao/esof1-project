package org.example.backend.repository;

import org.example.backend.model.LocalTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalTableRepository extends JpaRepository<LocalTable, UUID> {
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO tables (number, status) VALUES (:number, 0)")
    void insertLocalTable(@Param("number") int number);

    Optional<LocalTable> findByNumber(int number);
    Optional<LocalTable> findById(UUID id);

    @Query("SELECT COUNT(gt) FROM GuestTab gt WHERE gt.localTable.id = :tableId AND gt.timeOpened >= :startOfDay AND gt.timeOpened <= :endOfDay")
    int findGuestTabCountTodayById(@Param("tableId") UUID tableId,
                                   @Param("startOfDay") LocalDateTime startOfDay,
                                   @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(gt) FROM GuestTab gt WHERE gt.localTable.id = :tableId AND gt.status = org.example.backend.model.enums.GuestTabStatus.OPEN")
    int findOpenGuestTabCountById(@Param("tableId") UUID tableId);

}
