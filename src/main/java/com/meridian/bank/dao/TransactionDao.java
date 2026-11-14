package com.meridian.bank.dao;

import com.meridian.bank.model.LedgerEntry;
import com.meridian.bank.model.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface TransactionDao {
    void insert(Connection connection, LedgerEntry entry) throws SQLException;

    List<LedgerEntry> findByAccount(String accountNumber, TransactionType type, int limit) throws SQLException;

    Map<TransactionType, BigDecimal> summarize(String accountNumber) throws SQLException;
}
