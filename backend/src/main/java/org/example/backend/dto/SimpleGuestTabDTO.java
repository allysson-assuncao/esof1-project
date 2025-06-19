package org.example.backend.dto;

import lombok.Builder;

@Builder
public record SimpleGuestTabDTO(long id, String clientName) { }
