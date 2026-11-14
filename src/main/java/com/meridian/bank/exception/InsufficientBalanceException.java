package com.meridian.bank.exception;

import com.meridian.bank.util.Formats;

import java.math.BigDecimal;

public class InsufficientBalanceException extends BankException {
    private final BigDecimal available;
    private final BigDecimal requested;

    public InsufficientBalanceException(BigDecimal available, BigDecimal requested) {
        super("Insufficient balance. Available " + Formats.money(available)
                + ", requested " + Formats.money(requested) + ".");
        this.available = available;
        this.requested = requested;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getRequested() {
        return requested;
    }
}
