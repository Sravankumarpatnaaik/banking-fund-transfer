package com.meridian.bank.service;

import com.meridian.bank.dao.AccountDao;
import com.meridian.bank.dao.JdbcAccountDao;
import com.meridian.bank.dao.JdbcTransactionDao;
import com.meridian.bank.dao.TransactionDao;
import com.meridian.bank.exception.BankException;
import com.meridian.bank.exception.InsufficientBalanceException;
import com.meridian.bank.exception.InvalidAccountException;
import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.model.TransactionType;
import com.meridian.bank.sample.SampleData;
import com.meridian.bank.sample.SampleLedger;
import com.meridian.bank.sample.Schema;
import com.meridian.bank.util.AppConfig;
import com.meridian.bank.util.DataSourceProvider;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LedgerTransactionTest {
    private static HikariDataSource dataSource;
    private static AccountDao accounts;
    private static TransactionDao transactions;

    @BeforeAll
    static void connect() throws Exception {
        AppConfig config = new AppConfig(
                "jdbc:mysql://127.0.0.1:3306/meridian_bank_test?useSSL=false&allowPublicKeyRetrieval=true"
                        + "&characterEncoding=UTF-8&serverTimezone=UTC&connectionTimeZone=UTC",
                "bankuser",
                "meridian_local");
        dataSource = DataSourceProvider.create(config);
        try (Connection connection = dataSource.getConnection()) {
            connection.isValid(2);
        } catch (SQLException ex) {
            dataSource.close();
            dataSource = null;
            return;
        }
        Schema.migrate(dataSource);
        accounts = new JdbcAccountDao(dataSource);
        transactions = new JdbcTransactionDao(dataSource);
    }

    @AfterAll
    static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void clean() throws Exception {
        Assumptions.assumeTrue(dataSource != null, "MySQL test database is not reachable");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM transactions");
            statement.executeUpdate("DELETE FROM accounts");
            statement.executeUpdate("UPDATE account_sequence SET next_value = 1000000001");
        }
    }

    @Test
    void transferCommitsBothSidesWithOneReference() throws Exception {
        BankService bank = service();
        var from = bank.register(registration("Asha Rao", "asha.rao@meridian.test", "5000.00"));
        var to = bank.register(registration("Vikram Iyer", "vikram.iyer@meridian.test", "1000.00"));

        LedgerEntry debit = bank.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("250.00"), "Books");

        assertMoney("4750.00", bank.getAccount(from.getAccountNumber()).getBalance());
        assertMoney("1250.00", bank.getAccount(to.getAccountNumber()).getBalance());
        assertEquals(to.getAccountNumber(), debit.getCounterparty());
        assertEquals(debit.getReference(), only(bank.history(from.getAccountNumber(), TransactionType.TRANSFER_OUT)).getReference());
        assertEquals(debit.getReference(), only(bank.history(to.getAccountNumber(), TransactionType.TRANSFER_IN)).getReference());
    }

    @Test
    void shortWithdrawalRollsBack() throws Exception {
        BankService bank = service();
        var account = bank.register(registration("Meera Nair", "meera.nair@meridian.test", "800.00"));
        assertThrows(InsufficientBalanceException.class,
                () -> bank.withdraw(account.getAccountNumber(), new BigDecimal("800.01"), "Too much"));
        assertMoney("800.00", bank.getAccount(account.getAccountNumber()).getBalance());
        assertEquals(1, bank.history(account.getAccountNumber(), null).size());
    }

    @Test
    void failedCreditDoesNotLeaveTheDebitBehind() throws Exception {
        BankService bank = service();
        var from = bank.register(registration("Asha Rao", "asha.rao@meridian.test", "5000.00"));
        var to = bank.register(registration("Vikram Iyer", "vikram.iyer@meridian.test", "1000.00"));
        BankService risky = new BankService(accounts, new FailCreditInsert(transactions), dataSource);

        BankException error = assertThrows(BankException.class,
                () -> risky.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("250.00"), "Should not stick"));

        assertTrue(error.getMessage().contains("No balances were changed"), error.getMessage());
        assertMoney("5000.00", bank.getAccount(from.getAccountNumber()).getBalance());
        assertMoney("1000.00", bank.getAccount(to.getAccountNumber()).getBalance());
        assertEquals(1, bank.history(from.getAccountNumber(), null).size());
        assertEquals(1, bank.history(to.getAccountNumber(), null).size());
    }

    @Test
    void rejectsSelfTransferUnknownAccountAndDuplicateEmail() throws Exception {
        BankService bank = service();
        var account = bank.register(registration("Meera Nair", "meera.nair@meridian.test", "800.00"));
        assertThrows(InvalidAccountException.class,
                () -> bank.transfer(account.getAccountNumber(), account.getAccountNumber(), new BigDecimal("10.00"), null));
        assertThrows(InvalidAccountException.class,
                () -> bank.transfer(account.getAccountNumber(), "MB1000000999", new BigDecimal("10.00"), null));
        assertThrows(InvalidAccountException.class,
                () -> bank.register(registration("Meera Nair", "meera.nair@meridian.test", "500.00")));
        assertMoney("800.00", bank.getAccount(account.getAccountNumber()).getBalance());
    }

    @Test
    void depositAndFullWithdrawalUpdateTheBalance() throws Exception {
        BankService bank = service();
        var account = bank.register(registration("Meera Nair", "meera.nair@meridian.test", "800.00"));
        LedgerEntry deposit = bank.deposit(account.getAccountNumber(), new BigDecimal("10.50"), "Top up");
        assertMoney("810.50", deposit.getBalanceAfter());
        LedgerEntry withdrawal = bank.withdraw(account.getAccountNumber(), new BigDecimal("810.50"), "Close out");
        assertMoney("0.00", withdrawal.getBalanceAfter());
        assertMoney("0.00", bank.getAccount(account.getAccountNumber()).getBalance());
    }

    @Test
    void sampleLedgerMatchesThePublishedBalances() throws Exception {
        SampleLedger.seedIfEmpty(dataSource);
        BankService bank = service();
        assertMoney(SampleData.ANANYA_BALANCE.toPlainString(), bank.getAccount(SampleData.ANANYA).getBalance());
        assertMoney(SampleData.ROHAN_BALANCE.toPlainString(), bank.getAccount(SampleData.ROHAN).getBalance());
        assertEquals(3, bank.history(SampleData.ANANYA, null).size());
        assertEquals(3, bank.history(SampleData.ROHAN, null).size());
        assertTrue(bank.authenticate(SampleData.ANANYA, SampleData.PASSWORD).getHolderName().contains("Ananya"));
    }

    private static BankService service() {
        return new BankService(accounts, transactions, dataSource);
    }

    private static Registration registration(String name, String email, String opening) {
        return new Registration(name, email, "+91 9845011220", "SAVINGS", new BigDecimal(opening), "harbor-4821");
    }

    private static LedgerEntry only(List<LedgerEntry> entries) {
        assertEquals(1, entries.size());
        return entries.get(0);
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), actual.toPlainString());
    }

    private static final class FailCreditInsert implements TransactionDao {
        private final TransactionDao delegate;

        private FailCreditInsert(TransactionDao delegate) {
            this.delegate = delegate;
        }

        @Override
        public void insert(Connection connection, LedgerEntry entry) throws SQLException {
            if (entry.getType() == TransactionType.TRANSFER_IN) {
                throw new SQLException("simulated failure while recording the credit");
            }
            delegate.insert(connection, entry);
        }

        @Override
        public List<LedgerEntry> findByAccount(String accountNumber, TransactionType type, int limit) throws SQLException {
            return delegate.findByAccount(accountNumber, type, limit);
        }

        @Override
        public Map<TransactionType, BigDecimal> summarize(String accountNumber) throws SQLException {
            return delegate.summarize(accountNumber);
        }
    }
}
