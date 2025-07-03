package org.example.backend.dto.Product;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SimpleProductDTO(UUID id, String name) { }
