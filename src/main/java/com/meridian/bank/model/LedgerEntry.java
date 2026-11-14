package com.meridian.bank.model;

import com.meridian.bank.util.Formats;

import java.math.BigDecimal;
import java.sql.Timestamp;

public final class LedgerEntry {
    private final Long id;
    private final String reference;
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final String counterparty;
    private final String remarks;
    private final Timestamp createdAt;

    public LedgerEntry(
            Long id,
            String reference,
            String accountNumber,
            TransactionType type,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String counterparty,
            String remarks,
            Timestamp createdAt) {
        this.id = id;
        this.reference = reference;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.counterparty = counterparty;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getRemarks() {
        return remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getAmountDisplay() {
        return Formats.money(amount);
    }

    public String getSignedAmountDisplay() {
        String prefix = type.isCredit() ? "+ " : "− ";
        return prefix + Formats.money(amount);
    }

    public String getBalanceAfterDisplay() {
        return Formats.money(balanceAfter);
    }

    public String getWhenDisplay() {
        return Formats.when(createdAt);
    }

    public String getDetail() {
        String note = remarks == null ? "" : remarks.trim();
        return switch (type) {
            case TRANSFER_OUT -> join("To " + counterparty, note);
            case TRANSFER_IN -> join("From " + counterparty, note);
            case DEPOSIT -> note.isEmpty() ? "Deposit" : note;
            case WITHDRAWAL -> note.isEmpty() ? "Withdrawal" : note;
        };
    }

    private static String join(String primary, String note) {
        if (note.isEmpty() || note.equalsIgnoreCase("Transfer")) {
            return primary;
        }
        return primary + " · " + note;
    }
}
