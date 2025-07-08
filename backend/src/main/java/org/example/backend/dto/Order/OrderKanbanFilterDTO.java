package org.example.backend.dto.Order;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record OrderKanbanFilterDTO(Set<UUID> workstationIds) { }
