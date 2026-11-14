package com.meridian.bank.dao;

import com.meridian.bank.model.Account;
import com.meridian.bank.model.AccountType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public final class JdbcAccountDao implements AccountDao {
    private static final String COLUMNS =
            "account_number, holder_name, email, phone, password_hash, account_type, balance, status, created_at";

    private final DataSource dataSource;

    public JdbcAccountDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM accounts WHERE account_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                Account account = map(rows);
                account.concealSecret();
                return account;
            }
        }
    }

    @Override
    public Account findByLogin(String identifier) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM accounts WHERE account_number = ? OR email = ?";
        String trimmed = identifier == null ? "" : identifier.trim();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, trimmed.toUpperCase(Locale.ROOT));
            statement.setString(2, trimmed.toLowerCase(Locale.ROOT));
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                return map(rows);
            }
        }
    }

    @Override
    public Account findForUpdate(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM accounts WHERE account_number = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                return map(rows);
            }
        }
    }

    @Override
    public void insert(Connection connection, Account account) throws SQLException {
        String sql = "INSERT INTO accounts (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getHolderName());
            statement.setString(3, account.getEmail());
            statement.setString(4, account.getPhone());
            statement.setString(5, account.getPasswordHash());
            statement.setString(6, account.getAccountType().name());
            statement.setBigDecimal(7, account.getBalance());
            statement.setString(8, account.getStatus());
            statement.setTimestamp(9, account.getCreatedAt());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateBalance(Connection connection, String accountNumber, BigDecimal balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ? AND status = 'ACTIVE'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, balance);
            statement.setString(2, accountNumber);
            int updated = statement.executeUpdate();
            if (updated != 1) {
                throw new SQLException("Balance update did not match an active account.");
            }
        }
    }

    @Override
    public long allocateAccountSequence(Connection connection) throws SQLException {
        String select = "SELECT next_value FROM account_sequence WHERE name = 'account' FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(select);
             ResultSet rows = statement.executeQuery()) {
            if (!rows.next()) {
                throw new SQLException("Account sequence is not initialised.");
            }
            long current = rows.getLong(1);
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE account_sequence SET next_value = ? WHERE name = 'account'")) {
                update.setLong(1, current + 1);
                update.executeUpdate();
            }
            return current;
        }
    }

    @Override
    public int count(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM accounts");
             ResultSet rows = statement.executeQuery()) {
            rows.next();
            return rows.getInt(1);
        }
    }

    private static Account map(ResultSet rows) throws SQLException {
        return new Account(
                rows.getString("account_number"),
                rows.getString("holder_name"),
                rows.getString("email"),
                rows.getString("phone"),
                rows.getString("password_hash"),
                AccountType.valueOf(rows.getString("account_type")),
                rows.getBigDecimal("balance"),
                rows.getString("status"),
                rows.getTimestamp("created_at"));
    }
}
