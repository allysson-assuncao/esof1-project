package org.example.backend.dto;

import org.example.backend.model.enums.UserRole;

public record AuthResponseDTO(String username, String token, UserRole role) { }
