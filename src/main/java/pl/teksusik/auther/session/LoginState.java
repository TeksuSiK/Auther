package pl.teksusik.auther.session;

public enum LoginState {
    WAITING_FOR_PASSWORD, WAITING_FOR_TOTP, LOGGED_IN
}
