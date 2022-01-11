package pl.teksusik.auther.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import pl.teksusik.auther.storage.StorageType;

public class PluginConfiguration extends OkaeriConfig {
    @Comment("Choose type of data storage for plugin (MYSQL, SQLITE)")
    private StorageType storageType = StorageType.MYSQL;
    @Comment("MySQL connection data")
    private String mysqlHost = "0.0.0.0";
    private int mysqlPort = 3306;
    private String mysqlDatabase = "auther";
    private String mysqlUsername = "auther";
    private String mysqlPassword = "auther";
    @Comment("SQLite file name")
    private String sqliteFile = "auther.db";

    public StorageType getStorageType() {
        return storageType;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }
}
