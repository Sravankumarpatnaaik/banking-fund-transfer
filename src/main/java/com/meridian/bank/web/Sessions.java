package com.meridian.bank.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class Sessions {
    public static final String ACCOUNT = "accountNumber";
    public static final String HOLDER = "holderName";

    private Sessions() {
    }

    public static String accountNumber(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(ACCOUNT);
        return value instanceof String number ? number : null;
    }
}
