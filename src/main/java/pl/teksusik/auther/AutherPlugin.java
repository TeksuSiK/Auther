package pl.teksusik.auther;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import pl.teksusik.auther.account.Account;
import pl.teksusik.auther.account.repository.AccountRepository;
import pl.teksusik.auther.account.repository.impl.MySQLAccountRepository;
import pl.teksusik.auther.account.repository.impl.SQLiteAccountRepository;
import pl.teksusik.auther.account.service.AccountService;
import pl.teksusik.auther.configuration.AutherConfiguration;
import pl.teksusik.auther.configuration.MessageConfiguration;
import pl.teksusik.auther.configuration.MiniMessageTransformer;
import pl.teksusik.auther.configuration.StorageType;
import pl.teksusik.auther.message.MessageService;

import java.io.File;
import java.io.IOException;

public class AutherPlugin extends JavaPlugin {
    private final Logger logger = this.getSLF4JLogger();

    private final File autherConfigurationFile = new File(this.getDataFolder(), "config.yml");
    private AutherConfiguration autherConfiguration;

    private AccountRepository accountRepository;
    private AccountService accountService;

    private BukkitAudiences audiences;

    private final File messageConfigurationFile = new File(this.getDataFolder(), "messages.yml");
    private MessageConfiguration messageConfiguration;
    private MessageService messageService;


    @Override
    public void onEnable() {
        this.autherConfiguration = this.loadPluginConfiguration();
        this.messageConfiguration = this.loadMessageConfiguration();

        this.audiences = BukkitAudiences.create(this);
        this.messageService = new MessageService(this.logger, this.audiences);

        this.accountRepository = this.loadAccountRepository();
        this.accountService = new AccountService(this.accountRepository, this.messageService, this.messageConfiguration);
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
