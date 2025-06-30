package org.example.backend.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("admin"),
    CASHIER("caixa"),
    COOK("cozinheiro"),
    WAITER("garçom"),
    BARMAN("barista");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

}
