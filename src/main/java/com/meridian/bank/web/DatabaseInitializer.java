package com.meridian.bank.web;

import com.meridian.bank.App;
import com.meridian.bank.dao.JdbcAccountDao;
import com.meridian.bank.dao.JdbcTransactionDao;
import com.meridian.bank.sample.SampleData;
import com.meridian.bank.sample.SampleLedger;
import com.meridian.bank.sample.Schema;
import com.meridian.bank.service.BankService;
import com.meridian.bank.util.AppConfig;
import com.meridian.bank.util.DataSourceProvider;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.SQLException;
import java.util.logging.Logger;

public final class DatabaseInitializer implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(DatabaseInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AppConfig config = AppConfig.fromEnvironment();
        HikariDataSource dataSource = DataSourceProvider.create(config);
        try {
            prepare(dataSource);
        } catch (Exception ex) {
            dataSource.close();
            throw new IllegalStateException("Could not initialise the ledger database", ex);
        }
        ServletContext context = event.getServletContext();
        context.setAttribute(App.DATASOURCE, dataSource);
        context.setAttribute(App.SERVICE, new BankService(
                new JdbcAccountDao(dataSource),
                new JdbcTransactionDao(dataSource),
                dataSource));
        context.setAttribute("demoAccounts", SampleData.demos());
        context.setAttribute("demoPassword", SampleData.PASSWORD);
        LOG.info("Meridian ledger is ready.");
    }

    private static void prepare(HikariDataSource dataSource) throws SQLException, InterruptedException {
        SQLException last = null;
        for (int attempt = 1; attempt <= 30; attempt++) {
            try {
                Schema.migrate(dataSource);
                SampleLedger.seedIfEmpty(dataSource);
                return;
            } catch (SQLException ex) {
                last = ex;
                LOG.warning("Database not ready, attempt " + attempt + ": " + ex.getMessage());
                Thread.sleep(2000);
            }
        }
        throw last == null ? new SQLException("Database not ready") : last;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Object source = event.getServletContext().getAttribute(App.DATASOURCE);
        if (source instanceof HikariDataSource dataSource) {
            dataSource.close();
        }
    }
}
