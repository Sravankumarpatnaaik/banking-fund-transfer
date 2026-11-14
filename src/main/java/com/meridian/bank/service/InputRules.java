package com.meridian.bank.service;

import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.exception.InvalidAmountException;
import com.meridian.bank.model.AccountType;
import com.meridian.bank.util.Formats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

public final class InputRules {
    public static final BigDecimal MAX_MOVEMENT = new BigDecimal("1000000.00");
    public static final BigDecimal MIN_OPENING = new BigDecimal("500.00");
    private static final Pattern AMOUNT = Pattern.compile("\\d+(\\.\\d{1,2})?");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NAME = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,79}$");
    private static final Pattern ACCOUNT_NUMBER = Pattern.compile("MB\\d{10}");
    private static final Pattern REMARKS = Pattern.compile("[\\p{L}\\p{N} .,/'()-]{0,120}");

    private InputRules() {
    }

    public static String name(String raw) throws InvalidAccountException {
        String name = collapse(raw);
        if (!NAME.matcher(name).matches()) {
            throw new InvalidAccountException("Enter the account holder's name using letters, spaces, apostrophes, or hyphens.");
        }
        return name;
    }

    public static String email(String raw) throws InvalidAccountException {
        String email = raw == null ? "" : raw.trim().toLowerCase();
        if (!EMAIL.matcher(email).matches() || email.length() > 120) {
            throw new InvalidAccountException("Enter a valid email address.");
        }
        return email;
    }

    public static String phone(String raw) throws InvalidAccountException {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String phone = raw.trim().replaceAll("\\s+", " ");
        long digits = phone.chars().filter(Character::isDigit).count();
        if (digits < 8 || digits > 15 || !phone.matches("[0-9+ ]{8,20}")) {
            throw new InvalidAccountException("Enter a phone number with 8 to 15 digits, or leave it blank.");
        }
        return phone;
    }

    public static AccountType accountType(String raw) throws InvalidAccountException {
        AccountType type = AccountType.fromCode(raw);
        if (type == null) {
            throw new InvalidAccountException("Choose a Savings or Current account.");
        }
        return type;
    }

    public static String accountNumber(String raw) throws InvalidAccountException {
        String number = raw == null ? "" : raw.trim().toUpperCase();
        if (!ACCOUNT_NUMBER.matcher(number).matches()) {
            throw new InvalidAccountException("Account numbers look like MB1000000001.");
        }
        return number;
    }

    public static void password(String raw) throws BankException {
        if (raw == null || raw.length() < 8 || raw.length() > 72) {
            throw new BankException("Use a password of 8 to 72 characters.");
        }
    }

    public static String remarks(String raw) throws BankException {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String remarks = raw.trim().replaceAll("\\s+", " ");
        if (!REMARKS.matcher(remarks).matches()) {
            throw new BankException("Remarks can be up to 120 letters, numbers, and simple punctuation.");
        }
        return remarks;
    }

    public static BigDecimal amount(String raw) throws InvalidAmountException {
        if (raw == null || raw.isBlank()) {
            throw new InvalidAmountException("Enter an amount.");
        }
        String cleaned = raw.trim().replace(",", "").replace(" ", "");
        if (!AMOUNT.matcher(cleaned).matches()) {
            throw new InvalidAmountException("Enter a positive amount with at most two decimal places, such as 1500.50.");
        }
        return movement(new BigDecimal(cleaned));
    }

    public static BigDecimal movement(BigDecimal amount) throws InvalidAmountException {
        if (amount == null) {
            throw new InvalidAmountException("Enter an amount.");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException("Use at most two decimal places.");
        }
        BigDecimal scaled = amount.setScale(2, RoundingMode.UNNECESSARY);
        if (scaled.compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidAmountException("Enter at least ₹0.01.");
        }
        if (scaled.compareTo(MAX_MOVEMENT) > 0) {
            throw new InvalidAmountException("The maximum for one movement is " + Formats.money(MAX_MOVEMENT) + ".");
        }
        return scaled;
    }

    public static BigDecimal openingBalance(BigDecimal amount) throws InvalidAmountException {
        BigDecimal scaled = movement(amount);
        if (scaled.compareTo(MIN_OPENING) < 0) {
            throw new InvalidAmountException("The opening balance must be at least " + Formats.money(MIN_OPENING) + ".");
        }
        return scaled;
    }

    private static String collapse(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", " ");
    }
}
