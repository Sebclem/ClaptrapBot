package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.MainBot;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Disconnect from vocal chanel RestApi Command
 */
public class Disconnect implements CommandInterface{
    @Override
    public ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild) {
        AudioM.getInstance(guild).stop();
        return new ResponseEntity<>(new CommandResponseData(data.command,"Ok"), HttpStatus.OK);
    }

}
