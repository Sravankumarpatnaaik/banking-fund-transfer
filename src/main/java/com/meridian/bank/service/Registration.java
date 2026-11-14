package com.meridian.bank.service;

import com.meridian.bank.model.AccountType;

import java.math.BigDecimal;

public final class Registration {
    private final String holderName;
    private final String email;
    private final String phone;
    private final String accountType;
    private final BigDecimal openingBalance;
    private final String password;

    public Registration(
            String holderName,
            String email,
            String phone,
            String accountType,
            BigDecimal openingBalance,
            String password) {
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
        this.openingBalance = openingBalance;
        this.password = password;
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

    public String getAccountType() {
        return accountType;
    }

    public AccountType parsedType() {
        return AccountType.fromCode(accountType);
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public String getPassword() {
        return password;
    }
}
