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
 * Pause track RestApi command
 */
public class Pause implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        musicCommande.getAudioManager().getGuildMusicManager().scheduler.pause();
        return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
    }
}
