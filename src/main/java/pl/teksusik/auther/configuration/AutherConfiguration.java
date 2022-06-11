package pl.teksusik.auther.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class AutherConfiguration extends OkaeriConfig {
    @Comment("Choose type of data storage for plugin (MYSQL, SQLITE)")
    private StorageType storageType = StorageType.MYSQL;
    @Comment("MySQL connection data")
    private String host = "mysql";
    private int port = 3306;
    private String database = "auther";
    private String username = "auther";
    private String password = "auther";
    @Comment("SQLite file name")
    private String sqliteFile = "auther.db";

    @Comment("Server name displayed in Authenticator app")
    private String serverName = "auther";

    private int minimumPasswordLength = 8;
    private int maximumPasswordLength = 32;

    public StorageType getStorageType() {
        return storageType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }

    public String getServerName() {
        return serverName;
    }

    public int getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    public int getMaximumPasswordLength() {
        return maximumPasswordLength;
    }
}
