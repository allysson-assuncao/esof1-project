package org.example.backend.repository;

import org.example.backend.model.GuestTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestTabRepository extends JpaRepository<GuestTab, Long>, JpaSpecificationExecutor<GuestTab> {
    List<GuestTab> findByLocalTableId(UUID localTableId);
}
