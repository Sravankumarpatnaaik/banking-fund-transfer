package com.meridian.bank.service;

import com.meridian.bank.dao.AccountDao;
import com.meridian.bank.dao.TransactionDao;
import com.meridian.bank.exception.AuthenticationException;
import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InsufficientBalanceException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.exception.InvalidAmountException;
import com.meridian.bank.model.Account;
import com.meridian.bank.model.AccountType;
import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.model.TransactionType;
import com.meridian.bank.util.PasswordHasher;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BankService {
    private static final Logger LOG = Logger.getLogger(BankService.class.getName());
    private static final String GENERIC_LOGIN = "Those details don't match an account.";

    private final AccountDao accounts;
    private final TransactionDao transactions;
    private final DataSource dataSource;

    public BankService(AccountDao accounts, TransactionDao transactions, DataSource dataSource) {
        this.accounts = accounts;
        this.transactions = transactions;
        this.dataSource = dataSource;
    }

    public Account register(Registration request) throws BankException {
        String name = InputRules.name(request.getHolderName());
        String email = InputRules.email(request.getEmail());
        String phone = InputRules.phone(request.getPhone());
        AccountType type = InputRules.accountType(request.getAccountType());
        BigDecimal opening = InputRules.openingBalance(request.getOpeningBalance());
        InputRules.password(request.getPassword());
        String hash = PasswordHasher.hash(request.getPassword());

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            long sequence = accounts.allocateAccountSequence(connection);
            String number = "MB" + sequence;
            Timestamp now = Timestamp.from(Instant.now());
            Account account = new Account(number, name, email, phone, hash, type, opening, "ACTIVE", now);
            accounts.insert(connection, account);
            transactions.insert(connection, entry(number, TransactionType.DEPOSIT, opening, opening, null, "Account opening", now));
            connection.commit();
            account.concealSecret();
            return account;
        } catch (SQLException ex) {
            if (isDuplicate(ex)) {
                rollback(connection);
                throw new InvalidAccountException("An account with this email already exists.");
            }
            rollback(connection);
            LOG.log(Level.SEVERE, "Account opening failed", ex);
            throw new BankException("The account could not be opened. Nothing was saved.", ex);
        } finally {
            close(connection);
        }
    }

    private static boolean isDuplicate(SQLException exception) {
        SQLException current = exception;
        while (current != null) {
            if (current instanceof SQLIntegrityConstraintViolationException || current.getErrorCode() == 1062) {
                return true;
            }
            current = current.getNextException();
        }
        return false;
    }

    public Account authenticate(String identifier, String password) throws BankException {
        if (identifier == null || identifier.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Enter your account number or email, and your password.");
        }
        try {
            Account account = accounts.findByLogin(identifier);
            if (account == null) {
                PasswordHasher.verifyDummy(password);
                throw new AuthenticationException(GENERIC_LOGIN);
            }
            if (!PasswordHasher.verify(password, account.getPasswordHash())) {
                throw new AuthenticationException(GENERIC_LOGIN);
            }
            if (!"ACTIVE".equals(account.getStatus())) {
                throw new AuthenticationException("This account is not active.");
            }
            account.concealSecret();
            return account;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Sign-in lookup failed", ex);
            throw new BankException("Sign-in is unavailable right now. Try again in a moment.", ex);
        }
    }

    public Account getAccount(String accountNumber) throws BankException {
        try {
            Account account = accounts.findByNumber(accountNumber);
            if (account == null || !"ACTIVE".equals(account.getStatus())) {
                throw new InvalidAccountException("Your session does not match an active account.");
            }
            return account;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Balance enquiry failed", ex);
            throw new BankException("The balance could not be read. Try again in a moment.", ex);
        }
    }

    public LedgerEntry deposit(String accountNumber, BigDecimal amount, String remarks) throws BankException {
        BigDecimal credit = InputRules.movement(amount);
        String note = InputRules.remarks(remarks);
        return changeBalance(accountNumber, credit, TransactionType.DEPOSIT, null, note == null ? "Cash deposit" : note, false);
    }

    public LedgerEntry withdraw(String accountNumber, BigDecimal amount, String remarks) throws BankException {
        BigDecimal debit = InputRules.movement(amount);
        String note = InputRules.remarks(remarks);
        return changeBalance(accountNumber, debit, TransactionType.WITHDRAWAL, null, note == null ? "Cash withdrawal" : note, true);
    }

    public LedgerEntry transfer(String fromAccount, String toAccount, BigDecimal amount, String remarks) throws BankException {
        String source = InputRules.accountNumber(fromAccount);
        String destination = InputRules.accountNumber(toAccount);
        if (source.equals(destination)) {
            throw new InvalidAccountException("Transfers have to go to a different account.");
        }
        BigDecimal value = InputRules.movement(amount);
        String note = InputRules.remarks(remarks);
        String reference = newReference();
        Timestamp now = Timestamp.from(Instant.now());

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            // Lock the lower account number first so two opposite transfers cannot deadlock.
            String[] locked = TransferLocks.order(source, destination);
            Account first = requireActive(accounts.findForUpdate(connection, locked[0]));
            Account second = requireActive(accounts.findForUpdate(connection, locked[1]));
            Account from = source.equals(first.getAccountNumber()) ? first : second;
            Account to = destination.equals(first.getAccountNumber()) ? first : second;
            if (from.getBalance().compareTo(value) < 0) {
                throw new InsufficientBalanceException(from.getBalance(), value);
            }

            BigDecimal fromAfter = from.getBalance().subtract(value).setScale(2);
            BigDecimal toAfter = to.getBalance().add(value).setScale(2);
            accounts.updateBalance(connection, source, fromAfter);
            accounts.updateBalance(connection, destination, toAfter);
            LedgerEntry debit = new LedgerEntry(null, reference, source, TransactionType.TRANSFER_OUT, value, fromAfter, destination, note, now);
            LedgerEntry credit = new LedgerEntry(null, reference, destination, TransactionType.TRANSFER_IN, value, toAfter, source, note, now);
            transactions.insert(connection, debit);
            transactions.insert(connection, credit);
            connection.commit();
            return debit;
        } catch (InsufficientBalanceException | InvalidAccountException ex) {
            rollback(connection);
            throw ex;
        } catch (SQLException ex) {
            rollback(connection);
            LOG.log(Level.SEVERE, "Transfer failed and was rolled back", ex);
            throw new BankException("The transfer could not be completed. No balances were changed.", ex);
        } finally {
            close(connection);
        }
    }

    public List<LedgerEntry> history(String accountNumber, TransactionType type) throws BankException {
        return loadHistory(accountNumber, type, 50);
    }

    public List<LedgerEntry> recent(String accountNumber) throws BankException {
        return loadHistory(accountNumber, null, 6);
    }

    private List<LedgerEntry> loadHistory(String accountNumber, TransactionType type, int limit) throws BankException {
        try {
            return transactions.findByAccount(accountNumber, type, limit);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "History lookup failed", ex);
            throw new BankException("The transaction history could not be read.", ex);
        }
    }

    public TotalsView totals(String accountNumber) throws BankException {
        try {
            Map<TransactionType, BigDecimal> totals = transactions.summarize(accountNumber);
            return TotalsView.from(totals);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "History totals failed", ex);
            throw new BankException("The transaction totals could not be read.", ex);
        }
    }

    private LedgerEntry changeBalance(
            String accountNumber,
            BigDecimal amount,
            TransactionType type,
            String counterparty,
            String remarks,
            boolean debit) throws BankException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            Account account = requireActive(accounts.findForUpdate(connection, accountNumber));
            BigDecimal after;
            if (debit) {
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientBalanceException(account.getBalance(), amount);
                }
                after = account.getBalance().subtract(amount).setScale(2);
            } else {
                after = account.getBalance().add(amount).setScale(2);
                if (after.compareTo(InputRules.MAX_MOVEMENT.multiply(new BigDecimal("100"))) > 0) {
                    throw new InvalidAmountException("That deposit would push the balance past the ledger limit.");
                }
            }
            accounts.updateBalance(connection, account.getAccountNumber(), after);
            LedgerEntry entry = entry(account.getAccountNumber(), type, amount, after, counterparty, remarks, Timestamp.from(Instant.now()));
            transactions.insert(connection, entry);
            connection.commit();
            return entry;
        } catch (InsufficientBalanceException | InvalidAccountException | InvalidAmountException ex) {
            rollback(connection);
            throw ex;
        } catch (SQLException ex) {
            rollback(connection);
            LOG.log(Level.SEVERE, type.name().toLowerCase(Locale.ROOT) + " failed and was rolled back", ex);
            String noun = debit ? "withdrawal" : "deposit";
            throw new BankException("The " + noun + " could not be completed. Your balance was not changed.", ex);
        } finally {
            close(connection);
        }
    }

    private static Account requireActive(Account account) throws InvalidAccountException {
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            throw new InvalidAccountException("No active account exists with that number.");
        }
        return account;
    }

    private static LedgerEntry entry(
            String accountNumber,
            TransactionType type,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String counterparty,
            String remarks,
            Timestamp when) {
        return new LedgerEntry(null, newReference(), accountNumber, type, amount, balanceAfter, counterparty, remarks, when);
    }

    static String newReference() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase(Locale.ROOT);
    }

    private static void rollback(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Rollback failed", ex);
        }
    }

    private static void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Connection close failed", ex);
        }
    }
}
