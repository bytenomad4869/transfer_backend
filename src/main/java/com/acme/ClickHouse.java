package com.acme;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// @ApplicationScoped
public class ClickHouse {
    String url;
    String username;
    String password;

    private ClickHouseDataSource dataSource;

    public ClickHouse(String database) {
        Config config = ConfigProvider.getConfig();
        this.url = config.getValue("quarkus.datasource.jdbc.url", String.class);
        this.username = config.getValue("quarkus.datasource.username", String.class);
        this.password = config.getValue("quarkus.datasource.password", String.class);

        try {
            Properties info = new Properties();
            info.put("user", username);
            info.put("password", password);
            info.put("database", database);

            this.dataSource = new ClickHouseDataSource(url, info);
        } catch (SQLException e) {
            // TODO
        }
    }

    public void query(String sql) {
        try {
            Connection conn = dataSource.getConnection();

            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            // TODO
        }
    }
}
