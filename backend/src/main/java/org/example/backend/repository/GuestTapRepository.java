package org.example.backend.repository;

import org.example.backend.model.GuestTab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GuestTapRepository extends JpaRepository<GuestTab, UUID> {
}
