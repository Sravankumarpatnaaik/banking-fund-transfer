package com.meridian.bank.model;

public final class DemoAccount {
    private final String number;
    private final String name;
    private final String email;
    private final String typeLabel;
    private final String summary;

    public DemoAccount(String number, String name, String email, String typeLabel, String summary) {
        this.number = number;
        this.name = name;
        this.email = email;
        this.typeLabel = typeLabel;
        this.summary = summary;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public String getSummary() {
        return summary;
    }
}
