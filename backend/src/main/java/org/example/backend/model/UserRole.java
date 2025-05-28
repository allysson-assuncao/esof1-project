package org.example.backend.model;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("admin"),
    CASHIER("caixa"),
    WAITER("garçom");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

}
