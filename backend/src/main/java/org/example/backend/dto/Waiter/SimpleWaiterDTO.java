package org.example.backend.dto.Waiter;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SimpleWaiterDTO(UUID id, String userName) { }
