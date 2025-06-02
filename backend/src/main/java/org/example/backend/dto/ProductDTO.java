package org.example.backend.dto;

import java.util.List;
import java.util.UUID;

public record ProductDTO(UUID id, String name, String description, double price, boolean active, List<String> categoryNames) { }
