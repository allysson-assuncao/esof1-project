package org.example.backend.repository;

import org.example.backend.model.GuestTab;
import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.GuestTabStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestTabRepository extends JpaRepository<GuestTab, Long>, JpaSpecificationExecutor<GuestTab> {
    List<GuestTab> findByLocalTable(LocalTable localTable);
    List<GuestTab> findByLocalTableId(UUID localTableId);
    boolean existsByLocalTable_NumberAndStatus(int number, GuestTabStatus status);
}
