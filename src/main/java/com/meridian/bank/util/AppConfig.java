package com.meridian.bank.util;

public final class AppConfig {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public AppConfig(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public static AppConfig fromEnvironment() {
        return new AppConfig(
                env("DB_URL", "jdbc:mysql://127.0.0.1:3306/meridian_bank"
                        + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8"
                        + "&serverTimezone=UTC&connectionTimeZone=UTC"),
                env("DB_USER", "bankuser"),
                env("DB_PASSWORD", "meridian_local"));
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
