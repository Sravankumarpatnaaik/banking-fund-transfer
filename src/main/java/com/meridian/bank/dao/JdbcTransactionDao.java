package com.meridian.bank.dao;

import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.model.TransactionType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class JdbcTransactionDao implements TransactionDao {
    private final DataSource dataSource;

    public JdbcTransactionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Connection connection, LedgerEntry entry) throws SQLException {
        String sql = "INSERT INTO transactions "
                + "(txn_ref, account_number, txn_type, amount, balance_after, counterparty, remarks, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entry.getReference());
            statement.setString(2, entry.getAccountNumber());
            statement.setString(3, entry.getType().name());
            statement.setBigDecimal(4, entry.getAmount());
            statement.setBigDecimal(5, entry.getBalanceAfter());
            if (entry.getCounterparty() == null) {
                statement.setNull(6, Types.VARCHAR);
            } else {
                statement.setString(6, entry.getCounterparty());
            }
            if (entry.getRemarks() == null) {
                statement.setNull(7, Types.VARCHAR);
            } else {
                statement.setString(7, entry.getRemarks());
            }
            statement.setTimestamp(8, entry.getCreatedAt());
            statement.executeUpdate();
        }
    }

    @Override
    public List<LedgerEntry> findByAccount(String accountNumber, TransactionType type, int limit) throws SQLException {
        String sql = "SELECT id, txn_ref, account_number, txn_type, amount, balance_after, counterparty, remarks, created_at "
                + "FROM transactions WHERE account_number = ? AND (? IS NULL OR txn_type = ?) "
                + "ORDER BY created_at DESC, id DESC LIMIT ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            if (type == null) {
                statement.setNull(2, Types.VARCHAR);
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(2, type.name());
                statement.setString(3, type.name());
            }
            statement.setInt(4, limit);
            try (ResultSet rows = statement.executeQuery()) {
                List<LedgerEntry> entries = new ArrayList<>();
                while (rows.next()) {
                    entries.add(map(rows));
                }
                return List.copyOf(entries);
            }
        }
    }

    @Override
    public Map<TransactionType, BigDecimal> summarize(String accountNumber) throws SQLException {
        Map<TransactionType, BigDecimal> totals = new EnumMap<>(TransactionType.class);
        for (TransactionType type : TransactionType.values()) {
            totals.put(type, BigDecimal.ZERO.setScale(2));
        }
        String sql = "SELECT txn_type, COALESCE(SUM(amount), 0) AS total FROM transactions "
                + "WHERE account_number = ? GROUP BY txn_type";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    TransactionType type = TransactionType.fromCode(rows.getString("txn_type"));
                    if (type != null) {
                        totals.put(type, rows.getBigDecimal("total").setScale(2));
                    }
                }
            }
        }
        return totals;
    }

    private static LedgerEntry map(ResultSet rows) throws SQLException {
        return new LedgerEntry(
                rows.getLong("id"),
                rows.getString("txn_ref"),
                rows.getString("account_number"),
                TransactionType.valueOf(rows.getString("txn_type")),
                rows.getBigDecimal("amount"),
                rows.getBigDecimal("balance_after"),
                rows.getString("counterparty"),
                rows.getString("remarks"),
                rows.getTimestamp("created_at"));
    }
}
