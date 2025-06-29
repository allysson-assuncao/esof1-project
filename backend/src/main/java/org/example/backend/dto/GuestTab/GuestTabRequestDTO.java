package org.example.backend.dto.GuestTab;


import java.util.UUID;

public record GuestTabRequestDTO(UUID localTableId, String guestName) {
}
