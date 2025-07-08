package org.example.backend.dto.Workstation;

import lombok.Builder;
import org.example.backend.model.Workstation;

import java.util.UUID;

@Builder
public record SimpleWorkstationDTO(UUID id, String name) {
    public static SimpleWorkstationDTO fromEntity(Workstation w) {
        return new SimpleWorkstationDTO(w.getId(), w.getName());
    }
}
