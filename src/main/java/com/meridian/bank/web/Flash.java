package com.meridian.bank.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class Flash {
    private static final String ATTR = "flash";

    private Flash() {
    }

    public static void success(HttpServletRequest request, String message) {
        request.getSession().setAttribute(ATTR, new Message("success", message));
    }

    public static void pull(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object message = session.getAttribute(ATTR);
        if (message instanceof Message flash) {
            session.removeAttribute(ATTR);
            request.setAttribute("flash", flash);
        }
    }

    public static final class Message {
        private final String kind;
        private final String text;

        public Message(String kind, String text) {
            this.kind = kind;
            this.text = text;
        }

        public String getKind() {
            return kind;
        }

        public String getText() {
            return text;
        }
    }
}
