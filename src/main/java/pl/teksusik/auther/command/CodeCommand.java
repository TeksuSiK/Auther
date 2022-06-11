package pl.teksusik.auther.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;
import pl.teksusik.auther.session.SessionService;

public class CodeCommand implements CommandExecutor {
    private final SessionService sessionService;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;

    public CodeCommand(SessionService sessionService, MessageService messageService, MessageConfiguration messageConfiguration) {
        this.sessionService = sessionService;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 1) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getBadUsage()); //TODO Placeholder with correct usage
            return true;
        }

        if (sessionService.isLoggedIn(player.getUniqueId())) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getAlreadyLoggedIn());
            return true;
        }

        int code;
        try {
            code = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getBadUsage()); //TODO Placeholder with correct usage
            return true;
        }

        this.sessionService.verifyTotp(player.getUniqueId(), code);
        return true;
    }
}