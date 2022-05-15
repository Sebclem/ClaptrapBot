package net.Broken.RestApi.Commands;

import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Delete track RestApi command
 */
public class Dell implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild) {
        if (data.url != null) {
            if (AudioM.getInstance(guild).getGuildMusicManager().scheduler.remove(data.url)) {
                return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
            } else
                return new ResponseEntity<>(new CommandResponseData(data.command, "URL not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new CommandResponseData(data.command, "Missing URL"), HttpStatus.NOT_ACCEPTABLE);

    }
}
