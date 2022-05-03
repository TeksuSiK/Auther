package pl.teksusik.auther.session;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import pl.teksusik.auther.AutherPlugin;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;

public class SessionListener implements Listener {
    private final AutherPlugin plugin;
    private final SessionService sessionService;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;

    public SessionListener(AutherPlugin plugin, SessionService sessionService, MessageService messageService, MessageConfiguration messageConfiguration) {
        this.plugin = plugin;
        this.sessionService = sessionService;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getLoginReminder());
        }, 20L, 20L);
        this.sessionService.getLoginTaskMap().put(player.getUniqueId(), task);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.sessionService.logoutPlayer(player.getUniqueId());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            if (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX()) {
                event.setTo(event.getFrom());
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!this.sessionService.isLoggedIn(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!this.sessionService.isLoggedIn(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!this.sessionService.isLoggedIn(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (!this.sessionService.isLoggedIn(player.getUniqueId())) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!this.sessionService.isLoggedIn(event.getPlayer().getUniqueId())) {
            String[] args = event.getMessage().split(" ");
            if (!(args[0].equalsIgnoreCase("/login") || args[0].equalsIgnoreCase("/l") || args[0].equalsIgnoreCase("/log")
            || args[0].equalsIgnoreCase("/register") || args[0].equalsIgnoreCase("/reg"))) {
                event.setCancelled(true);
            }
        }
    }
}
