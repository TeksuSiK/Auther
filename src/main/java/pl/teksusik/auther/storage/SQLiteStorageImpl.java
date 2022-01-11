package pl.teksusik.auther.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class SQLiteStorageImpl implements Storage {
    private final HikariDataSource hikariDataSource;

    public SQLiteStorageImpl(File file, JavaPlugin javaPlugin) {
        this.hikariDataSource = new HikariDataSource();
        this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

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
