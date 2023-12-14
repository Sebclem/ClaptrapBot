package net.Broken.Api.Data.Settings;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettingGroup(
                String name,
                SettingDescriber mainField,
                List<SettingDescriber> fields) {
}
