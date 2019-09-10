package net.Broken.RestApi.Commands;

import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.WebLoadUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.http.ResponseEntity;

/**
 * Add track RestApi
 */
public class Add implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild) {
        return new WebLoadUtils(data, user, guild, true).getResponse();
    }
}
