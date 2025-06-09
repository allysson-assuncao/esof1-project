package org.example.backend.repository;

import org.example.backend.dto.LocalTableDTO;
import org.example.backend.model.LocalTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalTableRepository extends JpaRepository<LocalTable, UUID> {
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO tables (number, status) VALUES (:number, 0)")


    void insertLocalTable(@Param("number") int number);
    Optional<LocalTable> findById(UUID id);
    Optional<LocalTable> findByNumber(int number);

    @Query(value = """
        SELECT\s
            t.id as id,
            t.number as number,
            t.status as status,
            COALESCE(COUNT(gt.id), 0) as guestTabCountToday
        FROM tables t
        LEFT JOIN guest_tabs gt ON gt.local_table_id = t.id\s
            AND DATE(gt.time_opened) = CURRENT_DATE
        GROUP BY t.id, t.number, t.status
        ORDER BY t.number
       \s""", nativeQuery = true)
    List<LocalTableDTO> findAllWithGuestTabCountTodayRaw();


}
