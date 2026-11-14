package com.meridian.bank.web;

import com.meridian.bank.App;

import javax.sql.DataSource;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object source = getServletContext().getAttribute(App.DATASOURCE);
        if (!(source instanceof DataSource dataSource)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"status\":\"DOWN\"}");
            return;
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery("SELECT 1")) {
            rows.next();
            response.getWriter().write("{\"status\":\"UP\",\"database\":\"UP\"}");
        } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("{\"status\":\"DOWN\",\"database\":\"DOWN\"}");
        }
    }
}
