package org.example.backend.repository;

import org.example.backend.model.Workstation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkstationRepository extends JpaRepository<Workstation, UUID> {

}
