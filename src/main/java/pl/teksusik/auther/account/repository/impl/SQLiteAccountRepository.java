package pl.teksusik.auther.account.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class SQLiteAccountRepository extends SQLAccountRepository {
    public SQLiteAccountRepository(File file, Logger logger) {
        this.logger = logger;
        this.hikariDataSource = new HikariDataSource();

        this.hikariDataSource.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

        try {
            hikariDataSource.getConnection();
        } catch (SQLException exception) {
            this.logger.error("An error occurred while connecting to the database.", exception);
            return;
        }

        this.createTableIfNotExists();
    }
}