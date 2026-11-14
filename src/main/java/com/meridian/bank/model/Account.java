package com.meridian.bank.model;

import com.meridian.bank.util.Formats;

import java.math.BigDecimal;
import java.sql.Timestamp;

public final class Account {
    private final String accountNumber;
    private final String holderName;
    private final String email;
    private final String phone;
    private String passwordHash;
    private final AccountType accountType;
    private final BigDecimal balance;
    private final String status;
    private final Timestamp createdAt;

    public Account(
            String accountNumber,
            String holderName,
            String email,
            String phone,
            String passwordHash,
            AccountType accountType,
            BigDecimal balance,
            String status,
            Timestamp createdAt) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getBalanceDisplay() {
        return Formats.money(balance);
    }

    public String getOpenedDisplay() {
        return Formats.when(createdAt);
    }

    public void concealSecret() {
        this.passwordHash = null;
    }
}
