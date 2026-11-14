package com.meridian.bank.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class Csrf {
    public static final String FIELD = "_csrf";
    private static final String SESSION_ATTR = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() {
    }

    public static String token(HttpSession session) {
        Object current = session.getAttribute(SESSION_ATTR);
        if (current instanceof String existing && !existing.isEmpty()) {
            return existing;
        }
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String created = HexFormat.of().formatHex(bytes);
        session.setAttribute(SESSION_ATTR, created);
        return created;
    }

    public static boolean valid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(SESSION_ATTR);
        String provided = request.getParameter(FIELD);
        if (!(expected instanceof String expectedToken) || provided == null || expectedToken.length() != provided.length()) {
            return false;
        }
        int difference = 0;
        for (int i = 0; i < expectedToken.length(); i++) {
            difference |= expectedToken.charAt(i) ^ provided.charAt(i);
        }
        return difference == 0;
    }
}
