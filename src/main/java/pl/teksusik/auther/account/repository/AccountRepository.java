package pl.teksusik.auther.account.repository;

import pl.teksusik.auther.account.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    boolean registerAccount(Account account);
    boolean unregisterAccount(Account account);
    boolean isRegistered(UUID uuid);
    Optional<Account> findAccount(UUID uuid);
    boolean setSecretKey(UUID uuid, String secretKey);
}
