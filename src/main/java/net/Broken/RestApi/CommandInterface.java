package net.Broken.RestApi;

import net.Broken.Commands.Music;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import org.springframework.http.ResponseEntity;

public interface CommandInterface {
    ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data);
}
