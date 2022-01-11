package pl.teksusik.auther;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.teksusik.auther.configuration.PluginConfiguration;
import pl.teksusik.auther.storage.MySQLStorageImpl;
import pl.teksusik.auther.storage.SQLiteStorageImpl;
import pl.teksusik.auther.storage.Storage;
import pl.teksusik.auther.storage.StorageType;

import java.io.File;
import java.io.IOException;

public class AutherPlugin extends JavaPlugin {
    private PluginConfiguration pluginConfiguration;
    private Storage storage;

    @Override
    public void onEnable() {
        this.pluginConfiguration = this.registerPluginConfiguration();
        this.storage = this.registerStorage();
    }

    @Override
    public void onDisable() {
    }

    private PluginConfiguration registerPluginConfiguration() {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(new File(this.getDataFolder(), "config.yml"));
            it.saveDefaults();
            it.load(true);
        });
    }

    private Storage registerStorage() {
        File sqliteFile = new File(getDataFolder(), this.pluginConfiguration.getSqliteFile());
        if (this.pluginConfiguration.getStorageType().equals(StorageType.SQLITE)) {
            if (!sqliteFile.exists()) {
                try {
                    sqliteFile.createNewFile();
                } catch (IOException exception) {
                    this.getSLF4JLogger().error("An error occurred while creating the database file.", exception);
                    this.getServer().getPluginManager().disablePlugin(this);
                }
            }
        }

        switch (this.pluginConfiguration.getStorageType()) {
            case MYSQL:
                return new MySQLStorageImpl(this.pluginConfiguration.getMysqlHost(),
                    this.pluginConfiguration.getMysqlPort(),
                    this.pluginConfiguration.getMysqlDatabase(),
                    this.pluginConfiguration.getMysqlUsername(),
                    this.pluginConfiguration.getMysqlPassword(),
                    this);
            case SQLITE:
                return new SQLiteStorageImpl(sqliteFile, this);
            default:
                this.getSLF4JLogger().error("The storage type you entered is invalid.");
                this.getServer().getPluginManager().disablePlugin(this);
                return null;
        }
    }
}
