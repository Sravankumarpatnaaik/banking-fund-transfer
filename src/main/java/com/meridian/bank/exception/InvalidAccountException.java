package com.meridian.bank.exception;

public class InvalidAccountException extends BankException {
    public InvalidAccountException(String message) {
        super(message);
    }
}
