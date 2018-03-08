package net.Broken.RestApi;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.Broken.Commands.Music;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.*;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.Broken.audio.FindGeneral;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Rest Api Controller for /api/music
 */

@RestController
@RequestMapping("/api/music/")
public class MusicWebAPIController {
    Logger logger = LogManager.getLogger();
    @Autowired
    UserRepository userRepository;

    UserUtils userUtils = UserUtils.getInstance();


    @RequestMapping("/currentMusicInfo")
    public CurrentMusicData getCurrentM(){
        Music musicCommande = (Music) MainBot.commandes.get("music");

        if(musicCommande.audio.getGuild().getAudioManager().isConnected()){
            AudioPlayer player = musicCommande.audio.getGuildMusicManager().player;
            AudioTrack currentTrack = player.getPlayingTrack();
            if(currentTrack == null)
            {
                return new CurrentMusicData(null,0, "STOP",false, musicCommande.audio.getGuildMusicManager().scheduler.isAutoFlow());
            }
            UserAudioTrackData uat = new UserAudioTrackData(musicCommande.audio.getGuildMusicManager().scheduler.getCurrentPlayingTrack());
            return new CurrentMusicData(uat, currentTrack.getPosition(), currentTrack.getState().toString(), player.isPaused(), musicCommande.audio.getGuildMusicManager().scheduler.isAutoFlow());
        }else
        {
            return new CurrentMusicData(null,0, "DISCONNECTED",false, false);
        }
    }

    @RequestMapping("/getPlaylist")
    public PlaylistData getPlaylist(){
        Music musicCommande = (Music) MainBot.commandes.get("music");
        List<UserAudioTrackData> list = null;
        list = musicCommande.getAudioManager().getGuildMusicManager().scheduler.getList();
        return new PlaylistData(list);
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseData> command(@RequestBody CommandPostData data, HttpServletRequest request){

        if(data.command != null) {
            if(data.token != null) {
                try {
                    UserEntity user = userUtils.getUserWithApiToken(userRepository, data.token);
                    logger.info("receive command " + data.command + " from " + request.getRemoteAddr() + " USER: " + user.getName());
                    Music musicCommande = (Music) MainBot.commandes.get("music");

                    if (ApiCommandLoader.apiCommands.containsKey(data.command))
                        return ApiCommandLoader.apiCommands.get(data.command).action(musicCommande, data, MainBot.jda.getUserById(user.getJdaId()));
                    else
                        return new ResponseEntity<>(new CommandResponseData(data.command, "Unknown Command", "command"), HttpStatus.BAD_REQUEST);

                } catch (UnknownTokenException e) {
                    logger.warn("Command with unknown token from: "+request.getRemoteAddr());
                    return new ResponseEntity<>(new CommandResponseData(data.command,"Unknown Token!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);

                }

            }
            else{
                logger.warn("Command without token! ip: "+ request.getRemoteAddr());
                return new ResponseEntity<>(new CommandResponseData(data.command,"Missing token!\nPlease Re-connect.","token"), HttpStatus.UNAUTHORIZED);

            }
        }
        else
            logger.info("Null");
        return new ResponseEntity<>(new CommandResponseData(null, null), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/getChanel", method = RequestMethod.GET)
    public List<ChanelData> getChanel(){
        List<ChanelData> temp = new ArrayList<>();
        for(VoiceChannel aChanel : FindGeneral.find(MainBot.jda.getGuilds().get(0)).getVoiceChannels()){
            temp.add(new ChanelData(aChanel.getName(),aChanel.getId(),aChanel.getPosition()));
        }
        return temp;
    }


}

