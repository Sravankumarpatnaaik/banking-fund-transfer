package com.meridian.bank.model;

public enum TransactionType {
    DEPOSIT("Deposit", true),
    WITHDRAWAL("Withdrawal", false),
    TRANSFER_IN("Transfer in", true),
    TRANSFER_OUT("Transfer out", false);

    private final String label;
    private final boolean credit;

    TransactionType(String label, boolean credit) {
        this.label = label;
        this.credit = credit;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return name();
    }

    public boolean isCredit() {
        return credit;
    }

    public static TransactionType fromCode(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        for (TransactionType type : values()) {
            if (type.name().equalsIgnoreCase(raw.trim())) {
                return type;
            }
        }
        return null;
    }
}
