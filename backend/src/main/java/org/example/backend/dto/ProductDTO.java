package org.example.backend.dto;

import org.example.backend.model.enums.ProductDestination;

import java.util.List;
import java.util.UUID;

public record ProductDTO(String name, String description, double price, ProductDestination destination) { }
