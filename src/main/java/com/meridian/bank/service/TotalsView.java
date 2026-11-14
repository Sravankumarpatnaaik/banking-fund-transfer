package com.meridian.bank.service;

import com.meridian.bank.model.TransactionType;
import com.meridian.bank.util.Formats;

import java.math.BigDecimal;
import java.util.Map;

public final class TotalsView {
    private final String deposits;
    private final String withdrawals;
    private final String transferIn;
    private final String transferOut;

    private TotalsView(String deposits, String withdrawals, String transferIn, String transferOut) {
        this.deposits = deposits;
        this.withdrawals = withdrawals;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
    }

    public static TotalsView from(Map<TransactionType, BigDecimal> totals) {
        return new TotalsView(
                Formats.money(totals.get(TransactionType.DEPOSIT)),
                Formats.money(totals.get(TransactionType.WITHDRAWAL)),
                Formats.money(totals.get(TransactionType.TRANSFER_IN)),
                Formats.money(totals.get(TransactionType.TRANSFER_OUT)));
    }

    public String getDeposits() {
        return deposits;
    }

    public String getWithdrawals() {
        return withdrawals;
    }

    public String getTransferIn() {
        return transferIn;
    }

    public String getTransferOut() {
        return transferOut;
    }
}
