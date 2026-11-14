package com.meridian.bank.exception;

public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
