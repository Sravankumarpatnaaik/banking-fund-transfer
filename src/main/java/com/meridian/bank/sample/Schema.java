package com.meridian.bank.sample;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public final class Schema {
    private Schema() {
    }

    public static void migrate(DataSource dataSource) throws SQLException {
        String sql = read("/db/schema.sql");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String command : statements(sql)) {
                statement.execute(command);
            }
        }
    }

    static List<String> statements(String sql) {
        List<String> commands = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : sql.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }
            current.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                String command = current.toString().trim();
                command = command.substring(0, command.length() - 1).trim();
                if (!command.isEmpty()) {
                    commands.add(command);
                }
                current.setLength(0);
            }
        }
        if (!current.isEmpty() && !current.toString().isBlank()) {
            throw new IllegalStateException("Schema has a statement that does not end with a semicolon.");
        }
        return commands;
    }

    private static String read(String resource) throws SQLException {
        try (InputStream input = Schema.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new SQLException("Missing schema resource " + resource);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new SQLException("Could not read the schema.", ex);
        }
    }
}
