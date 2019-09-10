package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.Broken.audio.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AutoFlowOn implements CommandInterface{

    @Override
    public ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild) {
        AudioM audioM = AudioM.getInstance(guild);
        TrackScheduler scheduler = audioM.getGuildMusicManager().scheduler;
        scheduler.setAutoFlow(true);
        return new ResponseEntity<>(new CommandResponseData(data.command,"ok"), HttpStatus.OK);

    }
}
