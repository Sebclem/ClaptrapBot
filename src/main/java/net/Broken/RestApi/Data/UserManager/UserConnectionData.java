package net.Broken.RestApi.Data.UserManager;

public class UserConnectionData {
    public boolean accepted;
    public String token;
    public String message;

    public UserConnectionData(boolean accepted, String token, String message) {
        this.accepted = accepted;
        this.token = token;
        this.message = message;
    }
}
