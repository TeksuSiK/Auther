package pl.teksusik.auther.message;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

//TODO i18n
public class MessageService {
    private final Logger logger;
    private final BukkitAudiences audiences;

    public MessageService(Logger logger, BukkitAudiences audiences) {
        this.logger = logger;
        this.audiences = audiences;
    }

    public void sendMessage(UUID uuid, Component component) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            this.logger.error("Player with UUID {} is offline", uuid);
            return;
        }

        this.audiences.player(player).sendMessage(component);
    }
}
