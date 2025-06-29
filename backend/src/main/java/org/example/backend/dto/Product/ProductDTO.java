package org.example.backend.dto.Product;

import java.util.UUID;

public record ProductDTO(String name, String description, double price, UUID idCategory) { }
