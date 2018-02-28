package net.Broken.RestApi.Data.UserManager;

/**
 * Data for JSON Parsing
 */
public class UserConnectionData {
    public boolean accepted;
    public String token;
    public String message;
    public String error;
    public String name;

    public UserConnectionData(boolean accepted, String name, String token, String message) {
        this.accepted = accepted;
        this.token = token;
        this.message = message;
        this.name = name;
        this.error = null;
    }

    public UserConnectionData(boolean accepted, String message, String error) {
        this.accepted = accepted;
        this.token = null;
        this.message = message;
        this.error = error;
        this.name = null;
    }
}
