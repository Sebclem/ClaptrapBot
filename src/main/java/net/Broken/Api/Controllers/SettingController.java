package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Settings.SettingGroup;
import net.Broken.Api.Data.Settings.Value;
import net.Broken.Api.Services.SettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/setting")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SettingController {
    public final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("description")
    public List<SettingGroup> getSettingDescription(){
        return settingService.getSettingDescription();
    }

    @GetMapping("/{guildId}/values")
    @PreAuthorize("isInGuild(#guildId) && canManageGuild(#guildId)")
    public List<Value> getSettingValues(@PathVariable String guildId){
        return settingService.getValues(guildId);
    }
}
