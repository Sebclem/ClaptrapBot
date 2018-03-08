package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.dv8tion.jda.core.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Delete track RestApi command
 */
public class Dell implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        if(data.url != null) {
            if(musicCommande.getAudioManager().getGuildMusicManager().scheduler.remove(data.url)){
                return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
            }
            else
                return new ResponseEntity<>(new CommandResponseData(data.command,"URL not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new CommandResponseData(data.command, "Missing URL"), HttpStatus.NOT_ACCEPTABLE);

    }
}
