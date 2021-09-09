package dev.shitzuu.client.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.shitzuu.client.config.PrimaryConfig.StorageConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {

    private final HikariDataSource dataSource;
    private Connection connection;

    public DatabaseConnector(StorageConfig storageConfig) {
        this.dataSource = new HikariDataSource(this.getHikariConfig(storageConfig));
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    private HikariConfig getHikariConfig(StorageConfig storageConfig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
            storageConfig.getHostname(),
            storageConfig.getPort(),
            storageConfig.getDatabaseName()));
        hikariConfig.setUsername(storageConfig.getUsername());
        hikariConfig.setPassword(storageConfig.getPassword());

        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        return hikariConfig;
    }
}
