package pl.teksusik.auther.account;

import java.util.UUID;

public class Account {
    private final UUID uuid;
    private String password;
    private String secretKey;

    public Account(UUID uuid, String password, String secretKey) {
        this.uuid = uuid;
        this.password = password;
        this.secretKey = secretKey;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
