package org.example.backend.dto.LocalTable;

import org.example.backend.model.LocalTable;
import org.example.backend.model.enums.LocalTableStatus;

import java.util.UUID;

public class LocalTableGetDTO {
    UUID id;
    int number;
    LocalTableStatus status;
    public LocalTableGetDTO(LocalTable localTable) {
        this.id = localTable.getId();
        this.number = localTable.getNumber();
        this.status = localTable.getStatus();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalTableStatus getStatus() {
        return status;
    }

    public void setStatus(LocalTableStatus status) {
        this.status = status;
    }
}
