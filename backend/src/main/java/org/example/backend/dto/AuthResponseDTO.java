package org.example.backend.dto;

import org.example.backend.model.UserRole;

public record AuthResponseDTO(String username, String token, UserRole role) { }
