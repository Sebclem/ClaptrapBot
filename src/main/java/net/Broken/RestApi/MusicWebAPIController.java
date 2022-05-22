package net.Broken.RestApi;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.*;
import net.Broken.Tools.CacheTools;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.Stats.UserStatsUtils;
import net.Broken.Tools.UserManager.UserUtils;
import net.Broken.audio.AudioM;
import net.Broken.audio.GetVoiceChanels;
import net.Broken.audio.Youtube.SearchResult;
import net.Broken.audio.Youtube.YoutubeSearchRework;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rest Api Controller for /api/music
 */

@RestController
@RequestMapping("/api/music/")
public class MusicWebAPIController {
    private final
    UserRepository userRepository;
    Logger logger = LogManager.getLogger();
    UserUtils userUtils = UserUtils.getInstance();

    @Autowired
    public MusicWebAPIController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @RequestMapping("/currentMusicInfo")
    public ResponseEntity<CurrentMusicData> getCurrentM(@RequestParam(value = "guild") String guildId) { //TODO security issue ???!!!
        Guild guild = MainBot.jda.getGuildById(guildId);
        if (guild == null) {
            logger.warn("Request whit no guild!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            logger.trace("currentMusicInfo for " + guild.getName());
        }


        if (guild.getAudioManager().isConnected()) {
            AudioPlayer player = AudioM.getInstance(guild).getGuildMusicManager().player;
            AudioTrack currentTrack = player.getPlayingTrack();
            if (currentTrack == null) {
                return new ResponseEntity<>(new CurrentMusicData(null, 0, "STOP", false, AudioM.getInstance(guild).getGuildMusicManager().scheduler.isAutoFlow()), HttpStatus.OK);
            }
            UserAudioTrackData uat = new UserAudioTrackData(AudioM.getInstance(guild).getGuildMusicManager().scheduler.getCurrentPlayingTrack());
            return new ResponseEntity<>(new CurrentMusicData(uat, currentTrack.getPosition(), currentTrack.getState().toString(), player.isPaused(), AudioM.getInstance(guild).getGuildMusicManager().scheduler.isAutoFlow()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CurrentMusicData(null, 0, "DISCONNECTED", false, false), HttpStatus.OK);
        }
    }

    @RequestMapping("/getPlaylist")
    public ResponseEntity<PlaylistData> getPlaylist(@RequestParam(value = "guild") String guildId) { //TODO security issue ???!!!
        Guild guild = MainBot.jda.getGuildById(guildId);
        if (guild == null) {
            logger.warn("Request whit no guild!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            logger.trace("getPlaylist for " + guild.getName());
        }

        List<UserAudioTrackData> list = null;
        list = AudioM.getInstance(guild).getGuildMusicManager().scheduler.getList();
        return new ResponseEntity<>(new PlaylistData(list), HttpStatus.OK);
    }


    @RequestMapping("/getAllInfo")
    public ResponseEntity<AllMusicInfoData> getAllInfo(@RequestParam(value = "guild") String guildId, @CookieValue("token") String token) {
        if (token != null) {
            Guild guild = MainBot.jda.getGuildById(guildId);
            if (guild == null) {

                logger.warn("All info without guild!");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            try {
                UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
                logger.trace("All info from USER: " + user.getName() + " GUILD: " + guild.getName());

                PlaylistData list = new PlaylistData(AudioM.getInstance(guild).getGuildMusicManager().scheduler.getList());
                CurrentMusicData musicData;
                if (guild.getAudioManager().isConnected()) {
                    AudioPlayer player = AudioM.getInstance(guild).getGuildMusicManager().player;
                    AudioTrack currentTrack = player.getPlayingTrack();
                    if (currentTrack == null) {
                        musicData = new CurrentMusicData(null, 0, "STOP", false, AudioM.getInstance(guild).getGuildMusicManager().scheduler.isAutoFlow());
                    } else {
                        UserAudioTrackData uat = new UserAudioTrackData(AudioM.getInstance(guild).getGuildMusicManager().scheduler.getCurrentPlayingTrack());
                        musicData = new CurrentMusicData(uat, currentTrack.getPosition(), currentTrack.getState().toString(), player.isPaused(), AudioM.getInstance(guild).getGuildMusicManager().scheduler.isAutoFlow());
                    }

                } else {
                    musicData = new CurrentMusicData(null, 0, "DISCONNECTED", false, false);
                }
                return new ResponseEntity<>(new AllMusicInfoData(musicData, list), HttpStatus.OK);


            } catch (UnknownTokenException e) {
                logger.warn("All info with unknown token");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            }

        } else {
            logger.warn("All Info without token!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        }
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public ResponseEntity<CommandResponseData> command(@RequestBody CommandPostData data, HttpServletRequest request, @RequestParam(value = "guild") String guildId, @CookieValue("token") String token) {

        if (data.command != null) {
            if (token != null) {
                Guild guild = MainBot.jda.getGuildById(guildId);
                if (guild == null) {
                    logger.warn("Request whit no guild!");
                    return new ResponseEntity<>(new CommandResponseData(data.command, "Missing Guild!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);
                }

                try {
                    UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
                    logger.info("[" + guild.getName() + "] Receive command" + data.command + " from" + request.getRemoteAddr() + " USER:" + user.getName() + " GUILD:" + guild.getName());

                    if (ApiCommandLoader.apiCommands.containsKey(data.command)) {

                        UserStatsUtils.getINSTANCE().addApiCount(user, guildId);

                        return ApiCommandLoader.apiCommands.get(data.command).action(data, MainBot.jda.getUserById(user.getDiscordId()), guild);
                    } else
                        return new ResponseEntity<>(new CommandResponseData(data.command, "Unknown Command", "command"), HttpStatus.BAD_REQUEST);

                } catch (UnknownTokenException e) {
                    logger.warn("Command with unknown token from: " + request.getRemoteAddr());
                    return new ResponseEntity<>(new CommandResponseData(data.command, "Unknown Token!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);

                }

            } else {
                logger.warn("Command without token! ip: " + request.getRemoteAddr());
                return new ResponseEntity<>(new CommandResponseData(data.command, "Missing token!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);

            }
        } else
            logger.info("Null");
        return new ResponseEntity<>(new CommandResponseData(null, null), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/getChanel", method = RequestMethod.GET)
    public ResponseEntity<List<ChanelData>> getChanel(@CookieValue(name = "token", defaultValue = "") String token, @RequestParam(value = "guild") String guildId) {
        if (token != null && !token.isEmpty()) {
            try {
                UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
                Guild guild = MainBot.jda.getGuildById(guildId);
                if (guild == null) {
                    logger.warn("Request whit no guild!");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                Member member = guild.getMember(CacheTools.getJdaUser(user));
                if (member == null) {
                    member = guild.retrieveMember(CacheTools.getJdaUser(user)).complete();
                    if (member == null) {
                        logger.warn("Can't find member " + user.getName() + " for guild " + guild.getName() + ", User not in guild ?");
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                    }
                }
                logger.trace("getPlaylist for " + guild.getName());
                List<ChanelData> temp = new ArrayList<>();
                for (VoiceChannel aChanel : GetVoiceChanels.find(guild, member)) {
                    temp.add(new ChanelData(aChanel.getName(), aChanel.getId(), aChanel.getPosition()));
                }
                return new ResponseEntity<>(temp, HttpStatus.OK);
            } catch (UnknownTokenException e) {
                logger.warn("Get Chanel without token!");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } else {
            logger.warn("Get Chanel without token!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<SearchResult>> search(@CookieValue(name = "token", defaultValue = "") String token, @RequestParam(value = "query") String query) {
        if (token != null && !token.isEmpty()) {
            try {
                UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
                YoutubeSearchRework youtubeSearch = YoutubeSearchRework.getInstance();
                List<SearchResult> result = youtubeSearch.searchVideo(query, 25, false);
                return new ResponseEntity<>(result, HttpStatus.OK);

            } catch (UnknownTokenException e) {
                logger.warn("Search without token!");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } catch (GoogleJsonResponseException e) {
                logger.error("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (IOException e) {
                logger.catching(e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            }
        } else {
            logger.warn("Search without token!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


}

