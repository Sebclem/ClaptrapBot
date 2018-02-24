package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.MainBot;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.WebLoadUtils;
import net.dv8tion.jda.core.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Disconnect implements CommandInterface{
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        Music music = (Music) MainBot.commandes.get("music");
        music.audio.stop();
        return new ResponseEntity<>(new CommandResponseData(data.command,"Ok"), HttpStatus.OK);
    }

}