package com.meridian.bank.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class DataSourceProvider {
    private DataSourceProvider() {
    }

    public static HikariDataSource create(AppConfig config) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("MySQL JDBC driver is not on the classpath.", ex);
        }
        HikariConfig hikari = new HikariConfig();
        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setJdbcUrl(config.getJdbcUrl());
        hikari.setUsername(config.getUsername());
        hikari.setPassword(config.getPassword());
        hikari.setMaximumPoolSize(10);
        hikari.setMinimumIdle(1);
        hikari.setPoolName("meridian");
        hikari.setConnectionTimeout(5000);
        hikari.setInitializationFailTimeout(-1);
        return new HikariDataSource(hikari);
    }
}
