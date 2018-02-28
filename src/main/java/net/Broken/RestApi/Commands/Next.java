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
 * Next Track RestApi command
 */
public class Next implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        try {
            musicCommande.getAudioManager().getGuildMusicManager().scheduler.nextTrack();
            return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
        } catch (NullMusicManager | NotConnectedException nullMusicManager) {
            return new ResponseEntity<>(new CommandResponseData(data.command, "Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
