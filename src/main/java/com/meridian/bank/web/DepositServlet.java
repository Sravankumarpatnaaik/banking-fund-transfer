package com.meridian.bank.web;

import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InsufficientBalanceException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.exception.InvalidAmountException;
import com.meridian.bank.model.Account;
import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.service.InputRules;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DepositServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(DepositServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        show(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Account account = SignedIn.account(request, response);
            if (account == null) {
                return;
            }
            LedgerEntry entry = SignedIn.bank(request).deposit(
                    account.getAccountNumber(),
                    InputRules.amount(request.getParameter("amount")),
                    request.getParameter("remarks"));
            Flash.success(request, "Deposited " + entry.getAmountDisplay()
                    + ". Balance is now " + entry.getBalanceAfterDisplay()
                    + ". Reference " + entry.getReference() + ".");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (InvalidAmountException | InvalidAccountException | InsufficientBalanceException ex) {
            request.setAttribute("error", ex.getMessage());
            show(request, response);
        } catch (BankException ex) {
            LOG.log(Level.SEVERE, "Deposit failed", ex);
            request.setAttribute("error", ex.getMessage());
            show(request, response);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (request.getAttribute("account") == null && SignedIn.account(request, response) == null) {
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/deposit.jsp").forward(request, response);
        } catch (BankException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
