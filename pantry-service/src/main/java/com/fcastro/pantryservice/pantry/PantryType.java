package com.fcastro.pantryservice.pantry;

public enum PantryType {
    RECURRING("R"),
    NO_RECURRING("NR");

    public final String value;

    PantryType(String key) {
        this.value = key;
    }
}
