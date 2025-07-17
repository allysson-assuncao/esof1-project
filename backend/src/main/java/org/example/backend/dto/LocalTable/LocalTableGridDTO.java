package org.example.backend.dto.LocalTable;

import lombok.Builder;
import org.example.backend.model.enums.LocalTableStatus;

import java.util.UUID;

@Builder
public record LocalTableGridDTO(UUID id, int number, LocalTableStatus status, int guestTabCountToday) { }
