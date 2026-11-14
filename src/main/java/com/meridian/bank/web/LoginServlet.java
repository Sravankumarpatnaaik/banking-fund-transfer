package com.meridian.bank.web;

import com.meridian.bank.exception.AuthenticationException;
import com.meridian.bank.exception.BankException;
import com.meridian.bank.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoginServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Sessions.accountNumber(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Account account = SignedIn.bank(request).authenticate(
                    request.getParameter("identifier"),
                    request.getParameter("password"));
            HttpSession previous = request.getSession(false);
            if (previous != null) {
                previous.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(Sessions.ACCOUNT, account.getAccountNumber());
            session.setAttribute(Sessions.HOLDER, account.getHolderName());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (AuthenticationException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("identifier", request.getParameter("identifier"));
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (BankException ex) {
            LOG.log(Level.SEVERE, "Sign-in failed", ex);
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
