package pl.teksusik.auther.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.teksusik.auther.account.service.AccountService;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;

public class RegisterCommand implements CommandExecutor {
    private final AccountService accountService;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;

    public RegisterCommand(AccountService accountService, MessageService messageService, MessageConfiguration messageConfiguration) {
        this.accountService = accountService;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 2) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getBadUsage()); //TODO Placeholder with correct usage
            return true;
        }

        if (!args[0].equals(args[1])) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getPasswordNotMatch());
            return true;
        }

        this.accountService.registerAccount(player.getUniqueId(), args[0]);
        return true;
    }
}
