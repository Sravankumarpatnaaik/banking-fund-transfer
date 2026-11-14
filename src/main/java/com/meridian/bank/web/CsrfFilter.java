package com.meridian.bank.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class CsrfFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = Paths.of(request);
        if (Paths.asset(path) || "/health".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute("csrfToken", Csrf.token(request.getSession()));
        if ("POST".equalsIgnoreCase(request.getMethod()) && !Csrf.valid(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("error", "This form expired. Refresh the page and try again.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
