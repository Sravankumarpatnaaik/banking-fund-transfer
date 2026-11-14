package com.meridian.bank.dao;

import com.meridian.bank.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public interface AccountDao {
    Account findByNumber(String accountNumber) throws SQLException;

    Account findByLogin(String identifier) throws SQLException;

    Account findForUpdate(Connection connection, String accountNumber) throws SQLException;

    void insert(Connection connection, Account account) throws SQLException;

    void updateBalance(Connection connection, String accountNumber, BigDecimal balance) throws SQLException;

    long allocateAccountSequence(Connection connection) throws SQLException;

    int count(Connection connection) throws SQLException;
}
