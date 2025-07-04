package org.example.backend.dto;

import lombok.Builder;
import org.example.backend.model.enums.UserRole;

@Builder
public record AuthResponseDTO(String username, String email, UserRole role, String token) { }
