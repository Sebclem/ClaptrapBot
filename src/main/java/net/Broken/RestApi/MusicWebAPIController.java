package net.Broken.RestApi;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.Commands.Music;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.*;
import net.Broken.RestApi.Data.UserManager.CheckResposeData;
import net.Broken.RestApi.Data.UserManager.UserInfoData;
import net.Broken.Tools.UserManager.Exceptions.UserNotFoundException;
import net.Broken.audio.NotConectedException;
import net.Broken.audio.NullMusicManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// import net.Broken.DB.Repository.SavedPlaylistRepository;

@RestController
@RequestMapping("/api/music/")
public class MusicWebAPIController {
    Logger logger = LogManager.getLogger();
//    @Autowired
//    public SavedPlaylistRepository savedPlaylist;

    @RequestMapping("/currentMusicInfo")
    public CurrentMusicData getCurrentM(){
        Music musicCommande = (Music) MainBot.commandes.get("music");

        if(musicCommande.audio.getGuild().getAudioManager().isConnected()){
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
        }else
        {
            return new CurrentMusicData(null,0, "DISCONNECTED",false);
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

        if(data.command != null) {
            logger.info("receive command: " + data.command);
            Music musicCommande = (Music) MainBot.commandes.get("music");

            if(ApiCommandLoader.apiCommands.containsKey(data.command))
                return ApiCommandLoader.apiCommands.get(data.command).action(musicCommande,data);
            else
                return new ResponseEntity<>(new CommandResponseData(data.command,"Unknown Command"), HttpStatus.BAD_REQUEST);

        }
        else
            logger.info("Null");
        return new ResponseEntity<>(new CommandResponseData(null, null), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/getChanel", method = RequestMethod.GET)
    public List<Chanel> getChanel(){
        List<Chanel> temp = new ArrayList<>();
        for(VoiceChannel aChanel : MainBot.jda.getVoiceChannels()){
            temp.add(new Chanel(aChanel.getName(),aChanel.getId(),aChanel.getPosition()));
        }
        return temp;
    }





//    DB Test Ignore it

//    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    public ResponseEntity<String> test(){
//        SavedPlaylistEntity savedPlaylistEntity = new SavedPlaylistEntity();
//        savedPlaylistEntity.setAutorName("Test");
//        savedPlaylistEntity.setName("Playlist de test");
//        savedPlaylist.save(savedPlaylistEntity);
//        logger.info(savedPlaylistEntity);
//        return new ResponseEntity<String>("OK",HttpStatus.OK);
//    }
//    @GetMapping(path="/all")
//    public @ResponseBody Iterable<SavedPlaylistEntity> getAllUsers() {
//        // This returns a JSON or XML with the users
//        return savedPlaylist.findAll();
//    }


}

