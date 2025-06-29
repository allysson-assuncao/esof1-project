package org.example.backend.dto;

import java.util.List;
import java.util.UUID;


public record WorkstationRegisterDTO(String name, List<UUID> categoryIds) {
}
