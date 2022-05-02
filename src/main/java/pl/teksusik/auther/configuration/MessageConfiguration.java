package pl.teksusik.auther.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Pattern;

public class MessageConfiguration extends OkaeriConfig {
    @Exclude
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private Component accountAlreadyExists = this.miniMessage.deserialize("<dark_red>Error: <red>You already have an account.");
    private Component accountNotExists = this.miniMessage.deserialize("<dark_red>Error: <red>You do not have an account.");
    private Component accountRegisterError = this.miniMessage.deserialize("<dark_red>Error: <red>There was an error registering your account.");
    private Component accountRegisterSuccess = this.miniMessage.deserialize("<green>Your account has been successfully registered.");
    private Component accountUnregisterError = this.miniMessage.deserialize("<dark_red>Error: <red>There was an error unregistering your account.");
    private Component accountUnregisterSuccess = this.miniMessage.deserialize("<green>Your account has been successfully unregistered.");
    private Component incorrectPassword = this.miniMessage.deserialize("<dark_red>Error: <red>The password provided is incorrect.");
    private Component badUsage = this.miniMessage.deserialize("<dark_red>Error: <red>Bad usage.");
    private Component passwordNotMatch = this.miniMessage.deserialize("<dark_red>Error: <red>The passwords do not match.");

    public Component getAccountAlreadyExists() {
        return accountAlreadyExists;
    }

    public Component getAccountNotExists() {
        return accountNotExists;
    }

    public Component getAccountRegisterError() {
        return accountRegisterError;
    }

    public Component getAccountRegisterSuccess() {
        return accountRegisterSuccess;
    }

    public Component getAccountUnregisterError() {
        return accountUnregisterError;
    }

    public Component getAccountUnregisterSuccess() {
        return accountUnregisterSuccess;
    }

    public Component getIncorrectPassword() {
        return incorrectPassword;
    }

    public Component getBadUsage() {
        return badUsage;
    }

    public Component getPasswordNotMatch() {
        return passwordNotMatch;
    }

    @Exclude
    public static final Pattern LEGACY_COLOR_CODE_PATTERN = Pattern.compile("&([0-9A-Fa-fK-Ok-oRXrx][^&]*)");

    public static boolean containsLegacyColors(String string) {
        return LEGACY_COLOR_CODE_PATTERN.matcher(string).find();
    }
}
