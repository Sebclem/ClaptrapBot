package net.Broken.RestApi.Data.UserManager;

public class GuildInfo {
    public String name;
    public String id;
    public boolean isAdmin;
    public String imageUrl;

    public GuildInfo(String name, String id, boolean isAdmin, String imageUrl) {
        this.name = name;
        this.id = id;
        this.isAdmin = isAdmin;
        this.imageUrl = imageUrl;

    }
}
