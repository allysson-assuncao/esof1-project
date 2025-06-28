package org.example.backend.dto.Product;

import java.util.UUID;

public record ProductByCategoryDTO(UUID id,
                                   String name,
                                   String description,
                                   double price,
                                   UUID idCategory) {
}
