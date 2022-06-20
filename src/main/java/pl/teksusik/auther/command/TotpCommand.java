package pl.teksusik.auther.command;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.teksusik.auther.account.service.AccountService;
import pl.teksusik.auther.configuration.AutherConfiguration;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;

public class TotpCommand implements CommandExecutor {
    private final AccountService accountService;
    private final MessageService messageService;
    private final AutherConfiguration autherConfiguration;
    private final MessageConfiguration messageConfiguration;

    public TotpCommand(AccountService accountService, MessageService messageService, AutherConfiguration autherConfiguration, MessageConfiguration messageConfiguration) {
        this.accountService = accountService;
        this.messageService = messageService;
        this.autherConfiguration = autherConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 0 && args[0].equalsIgnoreCase("disable")) {
            this.accountService.setSecretKey(player.getUniqueId(), null);
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getDisabledTotp());
            return true;
        }

        if (this.accountService.hasTotpEnabled(player.getUniqueId())) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getTotpAlreadyEnabled());
            return true;
        }

        GoogleAuthenticatorKey authenticatorKey = this.accountService.generateSecretKey();

        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(this.autherConfiguration.getServerName(),
                player.getUniqueId().toString(),
                authenticatorKey);

        this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getQrCode().clickEvent(ClickEvent.openUrl(url)));

        this.accountService.addScratchCodes(player.getUniqueId(), authenticatorKey.getScratchCodes());
        this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getScratchKeys());
        authenticatorKey.getScratchCodes().forEach(scratchCode -> this.messageService.sendMessage(player.getUniqueId(), Component.text("- " + scratchCode)));

        this.accountService.setSecretKey(player.getUniqueId(), authenticatorKey.getKey());

        return true;
    }
}
