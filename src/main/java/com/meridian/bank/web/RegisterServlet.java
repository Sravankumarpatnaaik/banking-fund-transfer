package com.meridian.bank.web;

import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.exception.InvalidAmountException;
import com.meridian.bank.model.Account;
import com.meridian.bank.service.InputRules;
import com.meridian.bank.service.Registration;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RegisterServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(RegisterServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");
        if (password == null || !password.equals(confirm)) {
            request.setAttribute("error", "The two passwords don't match.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        try {
            Account account = SignedIn.bank(request).register(new Registration(
                    request.getParameter("holderName"),
                    request.getParameter("email"),
                    request.getParameter("phone"),
                    request.getParameter("accountType"),
                    InputRules.amount(request.getParameter("openingBalance")),
                    password));
            HttpSession previous = request.getSession(false);
            if (previous != null) {
                previous.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute(Sessions.ACCOUNT, account.getAccountNumber());
            session.setAttribute(Sessions.HOLDER, account.getHolderName());
            Flash.success(request, "Account " + account.getAccountNumber()
                    + " is open. Opening balance " + account.getBalanceDisplay() + ".");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (InvalidAmountException | InvalidAccountException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } catch (BankException ex) {
            LOG.log(Level.SEVERE, "Account opening failed", ex);
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
