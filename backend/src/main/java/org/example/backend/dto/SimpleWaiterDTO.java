package org.example.backend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SimpleWaiterDTO(UUID id, String userName) { }
