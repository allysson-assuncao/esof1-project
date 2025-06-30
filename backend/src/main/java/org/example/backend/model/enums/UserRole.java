package org.example.backend.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("admin"),
    CASHIER("caixa"),
    WAITER("garçom"),
    COOK("cozinheiro"),
    BARMAN("barista");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

}
