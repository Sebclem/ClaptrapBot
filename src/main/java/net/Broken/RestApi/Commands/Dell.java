package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.NotConectedException;
import net.Broken.audio.NullMusicManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Dell implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data) {
        if(data.url != null) {
            try {
                if(musicCommande.getAudioManager().getMusicManager().scheduler.remove(data.url)){
                    return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
                }
                else
                    return new ResponseEntity<>(new CommandResponseData(data.command,"URL not found"), HttpStatus.NOT_FOUND);
            } catch (NullMusicManager | NotConectedException nullMusicManager) {
                return new ResponseEntity<>(new CommandResponseData(data.command, "Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return new ResponseEntity<>(new CommandResponseData(data.command, "Missing URL"), HttpStatus.NOT_ACCEPTABLE);

    }
}
