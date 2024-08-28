package com.fcastro.accountservice.role;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER"),
    OWNER("OWNER");

    public final String value;

    RoleEnum(String value) {
        this.value = value;
    }
}
