package com.meridian.bank.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordAndFormatTest {
    @Test
    void hashesAndChecksPasswords() {
        String hash = PasswordHasher.hash("harbor-4821");
        assertTrue(PasswordHasher.verify("harbor-4821", hash));
        assertFalse(PasswordHasher.verify("wrong-password", hash));
        assertFalse(PasswordHasher.verify("harbor-4821", "not-a-hash"));
    }

    @Test
    void formatsRupeesAndIst() {
        String money = Formats.money(new BigDecimal("85000"));
        assertTrue(money.contains("₹"), money);
        assertTrue(money.contains("85"), money);
        assertTrue(money.contains("000"), money);
        assertTrue(Formats.greeting().startsWith("Good "));
    }
}
