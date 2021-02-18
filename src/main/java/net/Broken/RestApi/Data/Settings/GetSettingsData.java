package net.Broken.RestApi.Data.Settings;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSettingsData {
    public String description;
    public String name;
    public String id;
    public TYPE type;
    public List<Value> values;
    public String current;

    public GetSettingsData() {
    }

    public GetSettingsData(String name, String description, String id, TYPE type, List<Value> values, String current) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.type = type;
        this.values = values;
        this.current = current;
    }

    public enum TYPE{
        BOOL,LIST,STRING,SELECT_LIST
    }
}

