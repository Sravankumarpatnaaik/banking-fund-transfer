package com.meridian.bank.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formats {
    public static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH)
            .withZone(IST);
    private static final ThreadLocal<NumberFormat> MONEY = ThreadLocal.withInitial(Formats::newMoneyFormat);

    private Formats() {
    }

    public static String money(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        return MONEY.get().format(amount.setScale(2, RoundingMode.HALF_UP));
    }

    public static String when(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return WHEN.format(timestamp.toInstant()) + " IST";
    }

    public static String when(Instant instant) {
        if (instant == null) {
            return "";
        }
        return WHEN.format(instant) + " IST";
    }

    public static String greeting() {
        int hour = LocalTime.now(IST).getHour();
        if (hour < 12) {
            return "Good morning";
        }
        if (hour < 17) {
            return "Good afternoon";
        }
        return "Good evening";
    }

    private static NumberFormat newMoneyFormat() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("₹");
        format.setDecimalFormatSymbols(symbols);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format;
    }
}
