package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.LocalTableStatus;

import java.util.UUID;

@Builder
public record LocalTableDTO(UUID id, int number, LocalTableStatus status, int guestTabCountToday) implements Comparable<LocalTableDTO> {
    @Override
    public int compareTo(LocalTableDTO o) {
        return this.number() - o.number();
    }
}
