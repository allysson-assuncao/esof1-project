package org.example.backend.dto;

import lombok.Builder;

@Builder
public record UserDTO(String email, String password) { }
