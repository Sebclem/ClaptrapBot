package net.Broken.RestApi.Data.Settings;

public class Value {
    public String name;
    public String id;
    public boolean selected;

    public Value() {
    }

    public Value(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public Value(String name, String id, boolean selected) {
        this.name = name;
        this.id = id;
        this.selected = selected;
    }
}
