package org.example.backend.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record CategoryDTO(String name, boolean isMultiple, Set<CategoryDTO> subcategories, UUID workstationId) {}
