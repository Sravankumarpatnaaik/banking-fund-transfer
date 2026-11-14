package com.meridian.bank.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class AuthFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = Paths.of(request);
        if (needsSignIn(path) && Sessions.accountNumber(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (needsSignIn(path)) {
            response.setHeader("Cache-Control", "no-store");
        }
        chain.doFilter(request, response);
    }

    private static boolean needsSignIn(String path) {
        return "/dashboard".equals(path)
                || "/deposit".equals(path)
                || "/withdraw".equals(path)
                || "/transfer".equals(path)
                || "/history".equals(path);
    }
}
