package com.meridian.bank.web;

import jakarta.servlet.http.HttpServletRequest;

public final class Paths {
    private Paths() {
    }

    public static String of(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        if (uri.isEmpty()) {
            return "/";
        }
        return uri;
    }

    public static boolean asset(String path) {
        return path.startsWith("/css/") || "/favicon.svg".equals(path);
    }
}
