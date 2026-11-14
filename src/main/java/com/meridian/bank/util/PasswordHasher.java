package com.meridian.bank.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordHasher {
    private static final int COST = 10;
    private static final String DUMMY_HASH = BCrypt.withDefaults().hashToString(COST, "meridian-dummy-password".toCharArray());

    private PasswordHasher() {
    }

    public static String hash(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(password.toCharArray(), storedHash).verified;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public static void verifyDummy(String password) {
        if (password == null) {
            return;
        }
        verify(password, DUMMY_HASH);
    }
}
