package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.Broken.audio.TrackScheduler;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AutoFlowOn implements CommandInterface{

    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        AudioM audioM = AudioM.getInstance(null);
        try {
            TrackScheduler scheduler = audioM.getGuildMusicManager().scheduler;
            scheduler.setAutoFlow(true);
            return new ResponseEntity<>(new CommandResponseData(data.command,"ok"), HttpStatus.OK);
        } catch (NullMusicManager | NotConnectedException nullMusicManager) {
            LogManager.getLogger().catching(nullMusicManager);
            return new ResponseEntity<>(new CommandResponseData(data.command,"Not connected", "connect"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
