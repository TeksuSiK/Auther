package pl.teksusik.auther.account;

import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

public class PlayerListener implements Listener {
    private final JavaPlugin javaPlugin;
    private final AccountService accountService;

    public PlayerListener(JavaPlugin javaPlugin, AccountService accountService) {
         this.javaPlugin = javaPlugin;
         this.accountService = accountService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
         Player player = event.getPlayer();

         this.javaPlugin.getServer().getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
             Optional<String> optionalSecret = this.accountService.findSecretKey(player.getUniqueId());
             if (optionalSecret.isEmpty()) {
                  return;
             }

             if (optionalSecret.get().isEmpty() || optionalSecret.get().isBlank()) {
                  return;
             }

             BukkitTask task = this.javaPlugin.getServer().getScheduler().runTaskTimerAsynchronously(this.javaPlugin,
                  () -> player.sendMessage(ChatColor.GREEN + "Log in using /login <code>, with code from your TOTP app."), 20L, 20L * 5);
             this.accountService.getLoginTaskMap().put(player.getUniqueId(), task);
         });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
         Player player = event.getPlayer();
         this.accountService.getLoginTaskMap().remove(player.getUniqueId());
    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!this.accountService.isLoggedIn(event.getPlayer())) {
             if (event.getTo().getBlockZ() != event.getFrom().getBlockZ() || event.getTo().getBlockX() != event.getFrom().getBlockX()) {
                 event.setTo(event.getFrom());
             }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.accountService.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
         }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.accountService.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
         }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!this.accountService.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
         }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!this.accountService.isLoggedIn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!this.accountService.isLoggedIn(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!this.accountService.isLoggedIn(player)) {
                event.setCancelled(true);
             }
         }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!this.accountService.isLoggedIn(player)) {
                event.setCancelled(true);
             }
         }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
         if (event.getWhoClicked() instanceof Player player) {
             if (!this.accountService.isLoggedIn(player)) {
                 event.setCancelled(true);
                 event.getWhoClicked().closeInventory();
             }
         }
    }
}
