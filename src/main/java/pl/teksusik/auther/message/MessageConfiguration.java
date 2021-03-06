package pl.teksusik.auther.message;

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
    private Component incorrectTotp = this.miniMessage.deserialize("<dark_red>Error: <red>The TOTP password provided is incorrect.");
    private Component badUsage = this.miniMessage.deserialize("<dark_red>Error: <red>Bad usage.");
    private Component passwordNotMatch = this.miniMessage.deserialize("<dark_red>Error: <red>The passwords do not match.");
    private Component loginReminder = this.miniMessage.deserialize("<green>Login with /login <password>");
    private Component registerReminder = this.miniMessage.deserialize("<green>Register with /register <password> <password>");
    private Component alreadyLoggedIn = this.miniMessage.deserialize("<dark_red>Error: <red>You already logged in.");
    private Component loginSuccess = this.miniMessage.deserialize("<green>You have successfully logged in.");
    private Component passwordTooShort = this.miniMessage.deserialize("<dark_red>Error: <red>Your password is too short.");
    private Component passwordTooLong = this.miniMessage.deserialize("<dark_red>Error: <red>Your password is too long.");
    private Component totpReminder = this.miniMessage.deserialize("<green>Login with /code <TOTP password>");
    private Component totpAlreadyEnabled = this.miniMessage.deserialize("<dark_red>Error: <red>You already have TOTP login enabled");
    private Component totpNotEnabled = this.miniMessage.deserialize("<dark_red>Error: <red>You don't have TOTP login enabled");
    private Component qrCode = this.miniMessage.deserialize("<green>Your QR code URL is available <bold>here");
    private Component scratchKeys = this.miniMessage.deserialize("<green>Your scratch keys:");
    private Component disabledTotp = this.miniMessage.deserialize("<green>You have successfully disabled TOTP login");

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

    public Component getIncorrectTotp() {
        return incorrectTotp;
    }

    public Component getBadUsage() {
        return badUsage;
    }

    public Component getPasswordNotMatch() {
        return passwordNotMatch;
    }

    public Component getLoginReminder() {
        return loginReminder;
    }

    public Component getRegisterReminder() {
        return registerReminder;
    }

    public Component getAlreadyLoggedIn() {
        return alreadyLoggedIn;
    }

    public Component getLoginSuccess() {
        return loginSuccess;
    }

    public Component getPasswordTooShort() {
        return passwordTooShort;
    }

    public Component getPasswordTooLong() {
        return passwordTooLong;
    }

    public Component getTotpReminder() {
        return totpReminder;
    }

    public Component getTotpAlreadyEnabled() {
        return totpAlreadyEnabled;
    }

    public Component getTotpNotEnabled() {
        return totpNotEnabled;
    }

    public Component getQrCode() {
        return qrCode;
    }

    public Component getScratchKeys() {
        return scratchKeys;
    }

    public Component getDisabledTotp() {
        return disabledTotp;
    }

    @Exclude
    public static final Pattern LEGACY_COLOR_CODE_PATTERN = Pattern.compile("&([0-9A-Fa-fK-Ok-oRXrx][^&]*)");

    public static boolean containsLegacyColors(String string) {
        return LEGACY_COLOR_CODE_PATTERN.matcher(string).find();
    }
}
