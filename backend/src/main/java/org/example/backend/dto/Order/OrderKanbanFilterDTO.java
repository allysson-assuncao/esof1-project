package org.example.backend.dto.Order;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record OrderKanbanFilterDTO(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime, Set<UUID> workstationIds) { }
