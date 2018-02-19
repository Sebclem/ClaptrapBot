package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.WebLoadUtils;
import net.dv8tion.jda.core.entities.User;
import org.springframework.http.ResponseEntity;

public class Add implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        return new WebLoadUtils(musicCommande ,data, user).getResponse();
    }
}
