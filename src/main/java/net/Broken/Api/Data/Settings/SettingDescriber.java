package net.Broken.Api.Data.Settings;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettingDescriber(
        String id,
        String name,
        String description,
        TYPE type
) {

    public enum TYPE {
        BOOL, LIST, STRING, ROLE, TEXT_CHANNEL, VOICE_CHANNEL
    }
}
