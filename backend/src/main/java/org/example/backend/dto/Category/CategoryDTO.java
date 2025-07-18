package org.example.backend.dto.Category;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record CategoryDTO(String name, boolean isMultiple, boolean isAdditional, Set<String> subcategories, UUID workstationId) {}
