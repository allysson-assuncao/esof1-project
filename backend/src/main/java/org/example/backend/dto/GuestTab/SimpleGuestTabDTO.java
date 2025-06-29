package org.example.backend.dto.GuestTab;

import lombok.Builder;

@Builder
public record SimpleGuestTabDTO(long id, String clientName) { }
