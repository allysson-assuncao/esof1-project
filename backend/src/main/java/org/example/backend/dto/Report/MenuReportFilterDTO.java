package org.example.backend.dto.Report;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record MenuReportFilterDTO(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, @DateTimeFormat(pattern = "HH:mm") LocalTime businessDayStartTime, Set<UUID> categoryIds, Set<UUID> productIds, Double minPrice, Double maxPrice) {}
