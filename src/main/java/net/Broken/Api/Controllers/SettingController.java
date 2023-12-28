package net.Broken.Api.Controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.Broken.Api.Data.Settings.SettingGroup;
import net.Broken.Api.Data.Settings.Value;
import net.Broken.Api.Services.SettingService;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.Tools.Settings.SettingValueBuilder;

@RestController
@RequestMapping("/api/v2/setting")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SettingController {
    public final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("description")
    public List<SettingGroup> getSettingDescription() {
        return settingService.getSettingDescription();
    }

    @GetMapping("/{guildId}/values")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canManageGuild(#guildId)")
    public List<Value> getSettingValues(@PathVariable String guildId) {
        return settingService.getValues(guildId);
    }

    @PostMapping("/{guildId}/values")
    @PreAuthorize("@webSecurity.isInGuild(#guildId) && @webSecurity.canManageGuild(#guildId)")
    public List<Value> getSettingValues(@PathVariable String guildId, @RequestBody List<Value> values) {
        GuildPreferenceEntity pref = settingService.saveValue(guildId, values);
        return new SettingValueBuilder(pref).build();
    }
}
