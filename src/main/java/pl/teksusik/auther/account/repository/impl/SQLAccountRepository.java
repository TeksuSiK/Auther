package pl.teksusik.auther.account.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import pl.teksusik.auther.account.Account;
import pl.teksusik.auther.account.repository.AccountRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public abstract class SQLAccountRepository implements AccountRepository {
    protected Logger logger;
    protected HikariDataSource hikariDataSource;

    public void createTableIfNotExists() {
        String query = """
                CREATE TABLE IF NOT EXISTS `auther_accounts` (
                	`uuid` VARCHAR(36) NOT NULL,
                	`password` VARCHAR(256) NOT NULL,
                	`secret` VARCHAR(256)
                );
                """;
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            this.logger.error("An error occurred while creating the database tables.", exception);
        }
    }

    @Override
    public boolean registerAccount(Account account) {
        try (Connection connection = this.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `auther_accounts` (`uuid`, `password`) VALUES (?, ?)")) {
            preparedStatement.setString(1, account.getUuid().toString());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException exception) {
            this.logger.error("An error occurred while retrieving data from the database.", exception);
            return false;
        }
    }

    @Override
    public boolean unregisterAccount(Account account) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `auther_accounts` WHERE uuid = ?")) {
            preparedStatement.setString(1, account.getUuid().toString());
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException exception) {
            this.logger.error("An error occurred while retrieving data from the database.", exception);
            return false;
        }
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM `auther_accounts` WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            this.logger.error("An error occurred while retrieving data from the database.", exception);
            return false;
        }
    }

    @Override
    public Optional<Account> findAccount(UUID uuid) {
        try (Connection connection = this.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, password, secret FROM `auther_accounts` WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Account(
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("password"),
                            resultSet.getString("secret")));
                }
            }
        } catch (SQLException exception) {
            this.logger.error("An error occurred while retrieving data from the database.", exception);
        }

        return Optional.empty();
    }

    @Override
    public boolean setSecretKey(UUID uuid, String secretKey) {
        try (Connection connection = this.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `auther_accounts` SET secret = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, secretKey);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            this.logger.error("An error occurred while retrieving data from the database.", exception);
            return false;
        }
    }
}
