package pl.teksusik.auther.account.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import pl.teksusik.auther.account.Account;
import pl.teksusik.auther.account.repository.AccountRepository;
import pl.teksusik.auther.message.MessageConfiguration;
import pl.teksusik.auther.message.MessageService;
import pl.teksusik.auther.session.SessionService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountService {
    private final AccountRepository accountRepository;
    private final SessionService sessionService;
    private final MessageService messageService;
    private final MessageConfiguration messageConfiguration;
    private final GoogleAuthenticator authenticator;

    public AccountService(AccountRepository accountRepository, SessionService sessionService, MessageService messageService, MessageConfiguration messageConfiguration, GoogleAuthenticator authenticator) {
        this.accountRepository = accountRepository;
        this.sessionService = sessionService;
        this.messageService = messageService;
        this.messageConfiguration = messageConfiguration;
        this.authenticator = authenticator;
    }

    public CompletableFuture<Void> registerAccount(UUID uuid, String password) {
        return CompletableFuture.runAsync(() -> {
            if (this.accountRepository.isRegistered(uuid)) {
                this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountAlreadyExists());
                return;
            }

            String hashedPassword = BCrypt.withDefaults().hashToString(8, password.toCharArray());
            Account account = new Account(uuid, hashedPassword, null);
            if (!this.accountRepository.registerAccount(account)) {
                this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountRegisterError());
                return;
            }

            this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountRegisterSuccess());
            this.sessionService.loginPlayer(uuid, password, true);
        });
    }

    public CompletableFuture<Void> unregisterAccount(UUID uuid, String password) {
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

            if (!this.accountRepository.unregisterAccount(account)) {
                this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountUnregisterError());
                return;
            }

            this.messageService.sendMessage(uuid, this.messageConfiguration.getAccountUnregisterSuccess());
        });
    }

    public boolean hasTotpEnabled(UUID uuid) {
        Optional<Account> optionalAccount = this.accountRepository.findAccount(uuid);
        if (optionalAccount.isEmpty()) {
            return false;
        }
        Account account = optionalAccount.get();

        System.out.println(account.getSecretKey());
        return account.getSecretKey() != null;
    }


    public GoogleAuthenticatorKey generateSecretKey() {
        return this.authenticator.createCredentials();
    }

    public CompletableFuture<Void> setSecretKey(UUID uuid, String secretKey) {
        return CompletableFuture.runAsync(() -> {
            System.out.println(accountRepository.setSecretKey(uuid, secretKey));
        });
    }
}
