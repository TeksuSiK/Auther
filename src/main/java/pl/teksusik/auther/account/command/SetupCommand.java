package pl.teksusik.auther.account.command;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import pl.teksusik.auther.account.AccountService;

import java.util.Optional;

public class SetupCommand implements CommandExecutor {
    private final AccountService accountService;
    private final Logger logger;

    public SetupCommand(AccountService accountService, Logger logger) {
        this.accountService = accountService;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            this.logger.info("This command is for players only.");
            return true;
        }

        Optional<String> optionalSecret = this.accountService.findSecretKey(player.getUniqueId());
        if (optionalSecret.isPresent() && !optionalSecret.get().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You already have TOTP authentication configured.");
            return true;
        }

        String secretKey = new GoogleAuthenticator().createCredentials().getKey();
        this.accountService.setSecretKey(player.getUniqueId(), secretKey);
        player.sendMessage(ChatColor.GREEN + "Your QR code: " + this.accountService.getQRCodeURL(player, secretKey));

        return true;
    }
}
