package com.meridian.bank.web;

import com.meridian.bank.App;
import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.model.Account;
import com.meridian.bank.service.BankService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public final class SignedIn {
    private SignedIn() {
    }

    public static BankService bank(HttpServletRequest request) {
        return (BankService) request.getServletContext().getAttribute(App.SERVICE);
    }

    public static Account account(HttpServletRequest request, HttpServletResponse response) throws IOException, BankException {
        String number = Sessions.accountNumber(request);
        if (number == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        try {
            Account account = bank(request).getAccount(number);
            request.setAttribute("account", account);
            return account;
        } catch (InvalidAccountException ex) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
    }
}
