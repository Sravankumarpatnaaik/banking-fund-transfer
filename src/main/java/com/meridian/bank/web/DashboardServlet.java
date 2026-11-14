package com.meridian.bank.web;

import com.meridian.bank.exception.BankException;
import com.meridian.bank.model.Account;
import com.meridian.bank.util.Formats;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DashboardServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(DashboardServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Account account = SignedIn.account(request, response);
            if (account == null) {
                return;
            }
            Flash.pull(request);
            request.setAttribute("greeting", Formats.greeting());
            request.setAttribute("rows", SignedIn.bank(request).recent(account.getAccountNumber()));
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (BankException ex) {
            LOG.log(Level.SEVERE, "Dashboard failed", ex);
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
