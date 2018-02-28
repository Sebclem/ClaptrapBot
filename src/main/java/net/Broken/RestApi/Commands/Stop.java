package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Stop RestApi Command
 */
public class Stop implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        musicCommande.getAudioManager().stop((MessageReceivedEvent) null);
        return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);

    }
}
