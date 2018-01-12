package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.NotConectedException;
import net.Broken.audio.NullMusicManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Play implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data) {
        try {
            musicCommande.getAudioManager().getMusicManager().scheduler.resume();
            return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
        } catch (NullMusicManager | NotConectedException nullMusicManager) {
            return new ResponseEntity<>(new CommandResponseData(data.command, "Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
