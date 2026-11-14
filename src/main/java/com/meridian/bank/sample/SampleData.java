package com.meridian.bank.sample;

import com.meridian.bank.model.DemoAccount;
import com.meridian.bank.util.Formats;

import java.math.BigDecimal;
import java.util.List;

public final class SampleData {
    public static final String PASSWORD = "harbor-4821";
    public static final String ANANYA = "MB1000000001";
    public static final String ROHAN = "MB1000000002";
    public static final BigDecimal ANANYA_BALANCE = new BigDecimal("85000.00");
    public static final BigDecimal ROHAN_BALANCE = new BigDecimal("42650.50");

    private SampleData() {
    }

    public static List<DemoAccount> demos() {
        return List.of(
                new DemoAccount(
                        ANANYA,
                        "Ananya Sharma",
                        "ananya.sharma@meridian.demo",
                        "Savings",
                        "Seeded savings balance " + Formats.money(ANANYA_BALANCE) + "."),
                new DemoAccount(
                        ROHAN,
                        "Rohan Mehta",
                        "rohan.mehta@meridian.demo",
                        "Current",
                        "Seeded current balance " + Formats.money(ROHAN_BALANCE) + "."));
    }
}
