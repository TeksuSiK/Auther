package pl.teksusik.auther;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;
import pl.teksusik.auther.account.Account;
import pl.teksusik.auther.account.repository.AccountRepository;
import pl.teksusik.auther.account.repository.impl.MySQLAccountRepository;
import pl.teksusik.auther.account.repository.impl.SQLiteAccountRepository;
import pl.teksusik.auther.account.service.AccountService;
import pl.teksusik.auther.command.CodeCommand;
import pl.teksusik.auther.command.LoginCommand;
import pl.teksusik.auther.command.RecoveryCommand;
import pl.teksusik.auther.command.RegisterCommand;
import pl.teksusik.auther.command.TotpCommand;
import pl.teksusik.auther.command.UnregisterCommand;
import pl.teksusik.auther.configuration.AutherConfiguration;
import pl.teksusik.auther.configuration.StorageType;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;
import pl.teksusik.auther.message.MiniMessageTransformer;
import pl.teksusik.auther.session.SessionListener;
import pl.teksusik.auther.session.SessionService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class AutherPlugin extends JavaPlugin {
    private final Logger logger = this.getSLF4JLogger();

    private final File autherConfigurationFile = new File(this.getDataFolder(), "config.yml");
    private AutherConfiguration autherConfiguration;

    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private MessageConfiguration messageConfiguration;
    private MessageService messageService;

    private AccountRepository accountRepository;
    private AccountService accountService;

    private SessionService sessionService;

    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        try {
            this.autherConfiguration = this.loadPluginConfiguration();
            this.messageConfiguration = this.loadMessageConfiguration();
        } catch (OkaeriException exception) {
            this.logger.error("There was an error loading the configuration.", exception);
        }

        this.audiences = BukkitAudiences.create(this);
        this.messageService = new MessageService(this.logger, this.audiences);

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

        this.accountRepository = this.loadAccountRepository();
        this.sessionService = new SessionService(this, this.accountRepository, this.messageService, this.messageConfiguration, googleAuthenticator);
        this.accountService = new AccountService(this.accountRepository, this.sessionService, this.messageService, this.messageConfiguration, googleAuthenticator);

        this.getCommand("register").setExecutor(new RegisterCommand(this.accountService, this.autherConfiguration, this.messageService, this.messageConfiguration));
        this.getCommand("unregister").setExecutor(new UnregisterCommand(this.accountService, this.messageService, this.messageConfiguration));
        this.getCommand("login").setExecutor(new LoginCommand(this.sessionService, this.messageService, this.messageConfiguration));
        this.getCommand("code").setExecutor(new CodeCommand(this.accountService, this.sessionService, this.messageService, this.messageConfiguration));
        this.getCommand("totp").setExecutor(new TotpCommand(this.accountService, this.messageService, this.autherConfiguration, this.messageConfiguration));
        this.getCommand("recovery").setExecutor(new RecoveryCommand(this.accountService, this.sessionService, this.messageService, this.messageConfiguration));

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new SessionListener(this, accountRepository, this.sessionService, this.messageService, this.messageConfiguration), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitTask task = this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                Optional<Account> accountOptional = this.accountRepository.findAccount(player.getUniqueId());
                if (accountOptional.isPresent()) {
                    this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getLoginReminder());
                } else {
                    this.messageService.sendMessage(player.getUniqueId(), this.messageConfiguration.getRegisterReminder());
                }
            }, 20L, 20L);
            this.sessionService.getLoginTaskMap().put(player.getUniqueId(), task);
        }
    }

    @Override
    public void onDisable() {
    }

    private AutherConfiguration loadPluginConfiguration() {
        return ConfigManager.create(AutherConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withBindFile(this.autherConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    private MessageConfiguration loadMessageConfiguration() {
        return ConfigManager.create(MessageConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer());
            it.withSerdesPack(registry -> registry.register(new MiniMessageTransformer()));
            it.withBindFile(this.messageConfigurationFile);
            it.saveDefaults();
            it.load(true);
        });
    }

    private AccountRepository loadAccountRepository() {
        final File sqliteFile = new File(getDataFolder(), this.autherConfiguration.getSqliteFile());
        if (this.autherConfiguration.getStorageType().equals(StorageType.SQLITE)) {
            if (!sqliteFile.exists()) {
                try {
                    if (!sqliteFile.createNewFile()) {
                        this.logger.info("{} already exists.", sqliteFile.getName());
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return switch (this.autherConfiguration.getStorageType()) {
            case MYSQL -> new MySQLAccountRepository(this.autherConfiguration.getHost(),
                    this.autherConfiguration.getPort(),
                    this.autherConfiguration.getDatabase(),
                    this.autherConfiguration.getUsername(),
                    this.autherConfiguration.getPassword(),
                    this.logger);
            case SQLITE -> new SQLiteAccountRepository(sqliteFile, this.logger);
            default -> throw new IllegalArgumentException("The storage type you entered is invalid");
        };
    }
}
