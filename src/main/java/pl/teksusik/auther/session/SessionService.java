package pl.teksusik.auther.session;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.scheduler.BukkitTask;
import pl.teksusik.auther.AutherPlugin;
import pl.teksusik.auther.account.Account;
import pl.teksusik.auther.account.repository.AccountRepository;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SessionService {
    private final AutherPlugin plugin;
    private final AccountRepository accountRepository;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;
    private final GoogleAuthenticator authenticator;

    private final Map<UUID, BukkitTask> loginTaskMap = new HashMap<>();
    private final Map<UUID, BukkitTask> totpLoginTaskMap = new HashMap<>();

    public SessionService(AutherPlugin plugin, AccountRepository accountRepository, MessageService messageService, MessageConfiguration messageConfiguration, GoogleAuthenticator authenticator) {
        this.plugin = plugin;
        this.accountRepository = accountRepository;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
        this.authenticator = authenticator;
    }

    public CompletableFuture<Void> loginPlayer(UUID uuid, String password, boolean force) {
       return CompletableFuture.runAsync(() -> {
           Optional<Account> optionalAccount = this.accountRepository.findAccount(uuid);
           if (optionalAccount.isEmpty()) {
               this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountNotExists());
               return;
           }
           Account account = optionalAccount.get();

           if (!force) {
               BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), account.getPassword().toCharArray());
               if (!result.verified) {
                   this.messageService.sendMessage(uuid, this.messageConfiguration.getIncorrectPassword());
                   return;
               }

               if (account.getSecretKey() != null) {
                   BukkitTask task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
                       this.messageService.sendMessage(uuid, this.messageConfiguration.getTotpReminder());
                   }, 20L, 20L);
                   this.totpLoginTaskMap.put(uuid, task);

                   synchronized (this) {
                       if (this.loginTaskMap.containsKey(uuid)) {
                           this.loginTaskMap.get(uuid).cancel();
                           this.loginTaskMap.remove(uuid);
                       }
                   }
                   return;
               }
           }

           synchronized (this) {
               if (this.loginTaskMap.containsKey(uuid)) {
                   this.loginTaskMap.get(uuid).cancel();
                   this.loginTaskMap.remove(uuid);
               }

               if (this.totpLoginTaskMap.containsKey(uuid)) {
                   this.totpLoginTaskMap.get(uuid).cancel();
                   this.totpLoginTaskMap.remove(uuid);
               }
           }

           this.messageService.sendMessage(uuid, this.messageConfiguration.getLoginSuccess());
       });
   }

   public CompletableFuture<Void> verifyTotp(UUID uuid, int code) {
       return CompletableFuture.runAsync(() -> {
           Optional<Account> optionalAccount = this.accountRepository.findAccount(uuid);
           if (optionalAccount.isEmpty()) {
               this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountNotExists());
               return;
           }
           Account account = optionalAccount.get();

           boolean isCodeValid = authenticator.authorize(account.getSecretKey(), code);
           if (!isCodeValid) {
               this.messageService.sendMessage(uuid, this.messageConfiguration.getIncorrectTotp());
               return;
           }

           this.loginPlayer(uuid, "", true);
       });
   }

   public void logoutPlayer(UUID uuid) {
       if (!this.isLoggedIn(uuid)) {
           this.loginTaskMap.get(uuid).cancel();
           this.loginTaskMap.remove(uuid);
       }
   }

   public boolean isLoggedIn(UUID uuid) {
        return !this.loginTaskMap.containsKey(uuid) && !this.totpLoginTaskMap.containsKey(uuid);
   }

    public Map<UUID, BukkitTask> getLoginTaskMap() {
        return loginTaskMap;
    }

    public Map<UUID, BukkitTask> getTotpLoginTaskMap() {
        return totpLoginTaskMap;
    }
}
