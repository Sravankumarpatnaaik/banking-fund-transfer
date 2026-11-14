package com.meridian.bank.model;

public enum AccountType {
    SAVINGS("Savings"),
    CURRENT("Current");

    private final String label;

    AccountType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return name();
    }

    public static AccountType fromCode(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        for (AccountType type : values()) {
            if (type.name().equalsIgnoreCase(raw.trim()) || type.label.equalsIgnoreCase(raw.trim())) {
                return type;
            }
        }
        return null;
    }
}
