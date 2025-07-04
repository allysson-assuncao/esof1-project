package org.example.backend.dto.Product;

import java.util.UUID;

public record ProductRegisterDTO(String name, String description, double price, UUID idCategory) { }
