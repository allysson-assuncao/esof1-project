package org.example.backend.dto;
import lombok.Builder;
import org.example.backend.model.enums.GuestTabStatus;

import java.time.LocalDateTime;

@Builder
public record GuestTabGetDTO(Long id,
                             String name,
                             String status,
                             LocalDateTime timeOpened,
                             Integer tableNumber) {
    public GuestTabGetDTO(org.example.backend.model.GuestTab tab) {
        this(
                tab.getId(),
                tab.getGuestName(),
                tab.getStatus().name(),
                tab.getTimeOpened(),
                tab.getLocalTable().getNumber()
        );
    }
}
