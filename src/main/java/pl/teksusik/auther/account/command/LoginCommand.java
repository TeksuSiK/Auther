package pl.teksusik.auther.account.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import pl.teksusik.auther.account.AccountService;

import java.util.Optional;

public class LoginCommand implements CommandExecutor {
    private final AccountService accountService;
    private final Logger logger;

    public LoginCommand(AccountService accountService, Logger logger) {
        this.accountService = accountService;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            this.logger.info("This command is for players only.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Bad usage! Correct usage: /login <code>");
            return true;
        }

        if (accountService.isLoggedIn(player)) {
            player.sendMessage(ChatColor.RED + "You are already logged in.");
            return true;
        }

        Optional<String> optionalSecret = this.accountService.findSecretKey(player.getUniqueId());
        if (optionalSecret.isEmpty() || optionalSecret.get().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You do not have TOTP authentication enabled.");
            return true;
        }

        int code;
        try {
            code = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            player.sendMessage(ChatColor.RED + "Your code can only consist of numbers.");
            return true;
        }

        if (!this.accountService.isCodeValid(player, code)) {
            player.sendMessage(ChatColor.RED + "Your code is invalid.");
            return true;
        }

        this.accountService.getLoginTaskMap().get(player.getUniqueId()).cancel();
        this.accountService.getLoginTaskMap().remove(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You have successfully logged in.");
        return true;
    }
}
