package net.Broken.RestApi.Data.UserManager;

/**
 * Data for JSON Parsing
 */
public class CheckResposeData {
    public boolean accepted;
    public String name;
    public String message;
    public String id;

    public CheckResposeData(boolean accepted, String name, String message, String id) {
        this.accepted = accepted;
        this.name = name;
        this.message = message;
        this.id = id;
    }
}
