package com.meridian.bank.web;

import com.meridian.bank.exception.BankException;
import com.meridian.bank.model.Account;
import com.meridian.bank.model.TransactionType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HistoryServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(HistoryServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Account account = SignedIn.account(request, response);
            if (account == null) {
                return;
            }
            TransactionType type = TransactionType.fromCode(request.getParameter("type"));
            request.setAttribute("activeType", type == null ? "" : type.name());
            request.setAttribute("rows", SignedIn.bank(request).history(account.getAccountNumber(), type));
            request.setAttribute("totals", SignedIn.bank(request).totals(account.getAccountNumber()));
            request.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(request, response);
        } catch (BankException ex) {
            LOG.log(Level.SEVERE, "History failed", ex);
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
