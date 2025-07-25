package org.example.backend.dto.Category;

import java.util.Set;
import java.util.UUID;

public record HierarchicalCategoryDTO(UUID id,
                                      String name,
                                      boolean isAdditional,
                                      Set<HierarchicalCategoryDTO> subCategories) {
}