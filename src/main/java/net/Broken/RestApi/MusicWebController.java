package net.Broken.RestApi;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.Commandes.Music;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.RestApi.Data.CurrentMusicData;
import net.Broken.RestApi.Data.PlaylistData;
import net.Broken.audio.NotConectedException;
import net.Broken.audio.NullMusicManager;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/music/")
public class MusicWebController {
    Logger logger = LogManager.getLogger();

    @RequestMapping("/currentMusicInfo")
    public CurrentMusicData test(){
        Music musicCommande = (Music) MainBot.commandes.get("music");
        try {
            AudioPlayer player = musicCommande.audio.getMusicManager().player;
            AudioTrack currentTrack = player.getPlayingTrack();
            if(currentTrack == null)
            {
                return new CurrentMusicData(null,0, "STOP",false);
            }
            return new CurrentMusicData(currentTrack.getInfo(),currentTrack.getPosition(), currentTrack.getState().toString(), player.isPaused());
        } catch (NullMusicManager | NotConectedException nullMusicManager) {
            return new CurrentMusicData(null,0, "STOP",false);
        }
    }

    @RequestMapping("/getPlaylist")
    public PlaylistData getPlaylist(){
        Music musicCommande = (Music) MainBot.commandes.get("music");
        List<AudioTrackInfo> list = null;
        try {
            list = musicCommande.getAudioManager().getMusicManager().scheduler.getList();
            return new PlaylistData(list);
        } catch (NullMusicManager | NotConectedException nullMusicManager) {
            return new PlaylistData(list);
        }
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseData> command(@RequestBody CommandPostData data){

        if(data.command != null){
            logger.info("receive command: " + data.command);
            Music musicCommande = (Music) MainBot.commandes.get("music");
            switch (data.command){
                case "PLAY":
                    try {
                        musicCommande.getAudioManager().getMusicManager().scheduler.resume();
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Accepted"), HttpStatus.OK);
                    } catch (NullMusicManager | NotConectedException nullMusicManager) {
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
                    }

                case "PAUSE":
                    try {
                        musicCommande.getAudioManager().getMusicManager().scheduler.pause();
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Accepted"), HttpStatus.OK);
                    } catch (NullMusicManager | NotConectedException nullMusicManager) {
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
                    }

                case "NEXT":
                    try {
                        musicCommande.getAudioManager().getMusicManager().scheduler.nextTrack();
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Accepted"), HttpStatus.OK);
                    } catch (NullMusicManager | NotConectedException nullMusicManager) {
                        return new ResponseEntity<>(new CommandResponseData(data.command,"Not connected to vocal!"), HttpStatus.NOT_ACCEPTABLE);
                    }

                case "STOP":
                    musicCommande.getAudioManager().stop((MessageReceivedEvent) null);
                    return new ResponseEntity<>(new CommandResponseData(data.command,"Accepted"), HttpStatus.OK);

            }
        }
        else
            logger.info("Null");
        return new ResponseEntity<>(new CommandResponseData(null, null), HttpStatus.NO_CONTENT);
    }
}
