package pl.teksusik.auther.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.sql.SQLException;

public class MySQLStorageImpl implements Storage {
    private final HikariDataSource hikariDataSource;

    public MySQLStorageImpl(String host, int port, String database, String username, String password, JavaPlugin javaPlugin) {
        this.hikariDataSource = new HikariDataSource();

        this.hikariDataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        this.hikariDataSource.setUsername(username);
        this.hikariDataSource.setPassword(password);
        this.hikariDataSource.setAutoCommit(true);

        this.hikariDataSource.addDataSourceProperty("cachePrepStmts", true);
        this.hikariDataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.hikariDataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.hikariDataSource.addDataSourceProperty("useServerPrepStmts", true);
        this.hikariDataSource.addDataSourceProperty("useLocalSessionState", true);
        this.hikariDataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        this.hikariDataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        this.hikariDataSource.addDataSourceProperty("cacheServerConfiguration", true);
        this.hikariDataSource.addDataSourceProperty("elideSetAutoCommits", true);
        this.hikariDataSource.addDataSourceProperty("maintainTimeStats", false);
        this.hikariDataSource.addDataSourceProperty("autoClosePStmtStreams", true);
        this.hikariDataSource.addDataSourceProperty("useSSL", false);
        this.hikariDataSource.addDataSourceProperty("serverTimezone", "UTC");

        try {
            hikariDataSource.getConnection();
        } catch (SQLException exception) {
            javaPlugin.getSLF4JLogger().error("An error occurred while connecting to the database. Check if the given data is correct.", exception);
            javaPlugin.getServer().getPluginManager().disablePlugin(javaPlugin);
        }
    }

    @Override
    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }
}
