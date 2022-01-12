package pl.teksusik.auther.account;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;
import pl.teksusik.auther.storage.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    private final Storage storage;
    private final Logger logger;
    private final Map<UUID, BukkitTask> loginTaskMap = new HashMap<>();

    public AccountService(Storage storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;

        try (Connection connection = this.storage.getHikariDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `auther_accounts` (`uuid` VARCHAR(36) NOT NULL, `secretKey` VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (`uuid`))")) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            this.logger.error("An error occurred while communicating with the database.", exception);
        }
    }

    public Optional<String> findSecretKey(UUID uuid) {
        try (Connection connection = this.storage.getHikariDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT secretKey FROM auther_accounts WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.ofNullable(resultSet.getString("secretKey"));
            }
        } catch (SQLException exception) {
            this.logger.error("An error occurred while communicating with the database.", exception);
        }

        return Optional.empty();
    }

    public boolean setSecretKey(UUID uuid, String secret) {
        try (Connection connection = this.storage.getHikariDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO auther_accounts (uuid, secretKey) VALUES (?, ?) ON DUPLICATE KEY UPDATE secretKey = VALUES(secretKey)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, secret);
            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException exception) {
            this.logger.error("An error occurred while communicating with the database.", exception);
            return false;
        }
    }

    public Map<UUID, BukkitTask> getLoginTaskMap() {
        return loginTaskMap;
    }

    public boolean isLoggedIn(Player player) {
        return !this.getLoginTaskMap().containsKey(player.getUniqueId());
    }

    public boolean isCodeValid(Player player, int code) {
        return new GoogleAuthenticator().authorize(this.findSecretKey(player.getUniqueId()).get(), code);
    }

    public String getQRCodeURL(Player player, String secret) {
        String encodedPart = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%label%?secret=%key%&issuer=%title%";
        encodedPart = encodedPart.replaceAll("%label%", player.getName()).replaceAll("%title%", "Auther Minecraft").replaceAll("%key%", secret);

        return encodedPart;
    }
}
