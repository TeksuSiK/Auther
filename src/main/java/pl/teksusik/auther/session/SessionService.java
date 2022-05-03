package pl.teksusik.auther.session;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
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
    private final AccountRepository accountRepository;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;

    private final Map<UUID, BukkitTask> loginTaskMap = new HashMap<>();

    public SessionService(AccountRepository accountRepository, MessageService messageService, MessageConfiguration messageConfiguration) {
        this.accountRepository = accountRepository;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
    }

    public CompletableFuture<Void> loginPlayer(UUID uuid, String password) {
       return CompletableFuture.runAsync(() -> {
           Optional<Account> optionalAccount = this.accountRepository.findAccount(uuid);
           if (optionalAccount.isEmpty()) {
               this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountNotExists());
               return;
           }
           Account account = optionalAccount.get();

           BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), account.getPassword().toCharArray());
           if (!result.verified) {
               this.messageService.sendMessage(uuid, this.messageConfiguration.getIncorrectPassword());
               return;
           }

           synchronized (this) {
               this.loginTaskMap.get(uuid).cancel();
               this.loginTaskMap.remove(uuid);
           }

           this.messageService.sendMessage(uuid, this.messageConfiguration.getLoginSuccess());
       });
   }

   public void logoutPlayer(UUID uuid) {
       if (!this.isLoggedIn(uuid)) {
           this.loginTaskMap.get(uuid).cancel();
           this.loginTaskMap.remove(uuid);
       }
   }

   public boolean isLoggedIn(UUID uuid) {
        return !this.loginTaskMap.containsKey(uuid);
   }

    public Map<UUID, BukkitTask> getLoginTaskMap() {
        return loginTaskMap;
    }
}
