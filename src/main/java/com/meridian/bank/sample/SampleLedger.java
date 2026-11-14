package com.meridian.bank.sample;

import com.meridian.bank.dao.JdbcAccountDao;
import com.meridian.bank.dao.JdbcTransactionDao;
import com.meridian.bank.model.Account;
import com.meridian.bank.model.AccountType;
import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.model.TransactionType;
import com.meridian.bank.util.PasswordHasher;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class SampleLedger {
    private SampleLedger() {
    }

    public static void seedIfEmpty(DataSource dataSource) throws SQLException {
        JdbcAccountDao accounts = new JdbcAccountDao(dataSource);
        JdbcTransactionDao transactions = new JdbcTransactionDao(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            if (!lock(connection)) {
                throw new SQLException("Timed out waiting to seed the sample ledger.");
            }
            try {
                connection.setAutoCommit(false);
                if (accounts.count(connection) == 0) {
                    writeSeed(connection, accounts, transactions);
                }
                connection.commit();
            } catch (SQLException | RuntimeException ex) {
                connection.rollback();
                throw ex;
            } finally {
                unlock(connection);
            }
        }
    }

    private static void writeSeed(Connection connection, JdbcAccountDao accounts, JdbcTransactionDao transactions)
            throws SQLException {
        String hash = PasswordHasher.hash(SampleData.PASSWORD);
        Timestamp ananyaOpened = ist(2026, 9, 12, 10, 0);
        Timestamp rohanOpened = ist(2026, 9, 20, 12, 10);
        Account ananya = new Account(
                SampleData.ANANYA,
                "Ananya Sharma",
                "ananya.sharma@meridian.demo",
                "+91 98450 11220",
                hash,
                AccountType.SAVINGS,
                SampleData.ANANYA_BALANCE,
                "ACTIVE",
                ananyaOpened);
        Account rohan = new Account(
                SampleData.ROHAN,
                "Rohan Mehta",
                "rohan.mehta@meridian.demo",
                "+91 98200 44118",
                hash,
                AccountType.CURRENT,
                SampleData.ROHAN_BALANCE,
                "ACTIVE",
                rohanOpened);
        accounts.insert(connection, ananya);
        accounts.insert(connection, rohan);

        transactions.insert(connection, movement(SampleData.ANANYA, TransactionType.DEPOSIT, "50000.00", "50000.00", null, "Account opening", ananyaOpened, "TSEEDANANYAOPEN000001"));
        transactions.insert(connection, movement(SampleData.ANANYA, TransactionType.DEPOSIT, "40000.00", "90000.00", null, "Salary credit", ist(2026, 9, 18, 16, 20), "TSEEDANANYASALARY0001"));
        transactions.insert(connection, movement(SampleData.ANANYA, TransactionType.TRANSFER_OUT, "5000.00", "85000.00", SampleData.ROHAN, "Shared rent", ist(2026, 10, 3, 11, 5), "TSEEDRENT000000000001"));
        transactions.insert(connection, movement(SampleData.ROHAN, TransactionType.DEPOSIT, "30000.00", "30000.00", null, "Account opening", rohanOpened, "TSEEDROHANOPEN0000001"));
        transactions.insert(connection, movement(SampleData.ROHAN, TransactionType.TRANSFER_IN, "5000.00", "35000.00", SampleData.ANANYA, "Shared rent", ist(2026, 10, 3, 11, 5), "TSEEDRENT000000000001"));
        transactions.insert(connection, movement(SampleData.ROHAN, TransactionType.DEPOSIT, "7650.50", "42650.50", null, "Client receipt", ist(2026, 10, 21, 15, 40), "TSEEDROHANCLIENT00001"));

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE account_sequence SET next_value = 1000000003 WHERE name = 'account'")) {
            statement.executeUpdate();
        }
    }

    private static LedgerEntry movement(
            String account,
            TransactionType type,
            String amount,
            String balanceAfter,
            String counterparty,
            String remarks,
            Timestamp when,
            String reference) {
        return new LedgerEntry(
                null,
                reference,
                account,
                type,
                new BigDecimal(amount),
                new BigDecimal(balanceAfter),
                counterparty,
                remarks,
                when);
    }

    private static Timestamp ist(int year, int month, int day, int hour, int minute) {
        return Timestamp.from(LocalDateTime.of(year, month, day, hour, minute)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant());
    }

    private static boolean lock(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT GET_LOCK('meridian_seed', 30)");
             ResultSet rows = statement.executeQuery()) {
            return rows.next() && rows.getInt(1) == 1;
        }
    }

    private static void unlock(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT RELEASE_LOCK('meridian_seed')")) {
            statement.execute();
        } catch (SQLException ignored) {
            // The connection is about to close, which also drops the lock.
        }
    }
}
