package org.example.backend.repository;

import org.example.backend.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WorkstationRepository extends JpaRepository<Workstation, UUID> {
    @Query("SELECT w FROM Workstation w JOIN w.users u WHERE u.id = :userId")
    List<Workstation> findWorkstationsByUserId(@Param("userId") UUID userId);

}
