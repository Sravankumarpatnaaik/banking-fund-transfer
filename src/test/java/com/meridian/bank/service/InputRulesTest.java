package com.meridian.bank.service;

import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputRulesTest {
    @Test
    void parsesGroupedAmounts() throws Exception {
        assertEquals(0, new BigDecimal("1500.50").compareTo(InputRules.amount("1,500.50")));
        assertEquals(0, new BigDecimal("500.00").compareTo(InputRules.amount("500")));
    }

    @Test
    void rejectsBadAmounts() {
        assertThrows(InvalidAmountException.class, () -> InputRules.amount("0"));
        assertThrows(InvalidAmountException.class, () -> InputRules.amount("10.999"));
        assertThrows(InvalidAmountException.class, () -> InputRules.amount("1000000.01"));
        assertThrows(InvalidAmountException.class, () -> InputRules.amount("abc"));
        assertThrows(InvalidAmountException.class, () -> InputRules.openingBalance(new BigDecimal("499.99")));
    }

    @Test
    void normalisesAccountNumbers() throws Exception {
        assertEquals("MB1000000001", InputRules.accountNumber(" mb1000000001 "));
        assertThrows(InvalidAccountException.class, () -> InputRules.accountNumber("1000000001"));
    }
}
