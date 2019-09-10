package net.Broken.RestApi;

import net.Broken.Commands.Music;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.http.ResponseEntity;


/**
 * Represent RestApi command
 */
public interface CommandInterface {
    /**
     * Main action
     * @param musicCommande Current guild music command
     * @param data Received data
     * @param user User who submit RestApi command
     * @param guild
     * @return HTTP Response
     */
    ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild);
}
