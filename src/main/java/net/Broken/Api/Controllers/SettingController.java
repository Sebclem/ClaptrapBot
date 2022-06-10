package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Settings.SettingGroup;
import net.Broken.Api.Services.SettingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
