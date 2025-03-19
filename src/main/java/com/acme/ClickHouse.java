package com.acme;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// @ApplicationScoped
public class ClickHouse {
    // @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String url = "jdbc:clickhouse://localhost:18123/transfer";
    // @ConfigProperty(name = "quarkus.datasource.username")
    String username = "default";
    // @ConfigProperty(name = "quarkus.datasource.password")
    String password = "root1234";

    private ClickHouseDataSource dataSource;

    public ClickHouse(String database) {
        try {
            Properties info = new Properties();
            info.put("user", username);
            info.put("password", password);
            info.put("database", database);

            this.dataSource = new ClickHouseDataSource(url, info);
        } catch (SQLException e){
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
