package org.example.backend.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("admin"),
    CASHIER("caixa"),
    COOK("cozinheiroz"),
    WAITER("garçom");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

}
