package org.example.backend.dto.Order;

import lombok.Builder;
import org.example.backend.dto.DrillDownOrderDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderGroupDTO(LocalDateTime representativeTime, double groupTotalPrice, int numberOfItems, Set<DrillDownOrderDTO> orders) {}
