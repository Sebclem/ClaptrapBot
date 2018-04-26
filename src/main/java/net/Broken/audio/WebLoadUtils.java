package net.Broken.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Interface between WebApi and Music bot for submitting track
 */
public class WebLoadUtils {
    private ResponseEntity<CommandResponseData> response;
    private Logger logger = LogManager.getLogger();
    public UserAudioTrack userAudioTrack;

    /**
     * Submit a track or playlist to Music bot
     * @param data Received data from API
     * @param user User who submit the track
     * @param guild
     */
    public WebLoadUtils(CommandPostData data, User user, Guild guild, boolean submit){
        AudioPlayerManager playerM = AudioM.getInstance(guild).getPlayerManager();

        try {

            AudioM audioM = AudioM.getInstance(guild);
            playerM.loadItemOrdered(audioM.getGuildMusicManager(), data.url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    logger.info("Single Track detected from web!");

                    userAudioTrack = new UserAudioTrack(user, track);
                    if(submit)
                        audioM.play(audioM.getGuild(), audioM.getPlayedChanel(), audioM.getGuildMusicManager(), userAudioTrack, data.onHead);
                    response = new ResponseEntity<>(new CommandResponseData("ADD", "Loaded"), HttpStatus.OK);

                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {

                    if(submit)
                    {
                        logger.info("Playlist detected from web! Limit: " + data.playlistLimit);
                        audioM.playListLoader(playlist, data.playlistLimit, user, data.onHead);
                        response = new ResponseEntity<>(new CommandResponseData("ADD", "Loaded"), HttpStatus.OK);
                    }else
                    {
                        response = new ResponseEntity<>(new CommandResponseData("ADD", "Adding a list on saved playlist is currently not supported"), HttpStatus.NOT_ACCEPTABLE);

                    }

                }

                @Override
                public void noMatches() {
                    logger.warn("Cant find media ! (web) url: "+ data.url);
                    response = new ResponseEntity<>(new CommandResponseData("ADD", "Can't find media!"), HttpStatus.NOT_FOUND);

                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    logger.error("Cant load media ! (web)");
                    response = new ResponseEntity<>(new CommandResponseData("ADD", "Cant load media !"), HttpStatus.INTERNAL_SERVER_ERROR);

                }
            });
            while(response == null)
                Thread.sleep(10);

        } catch (InterruptedException nullMusicManager) {
            nullMusicManager.printStackTrace();
        }
    }

    /**
     * Wait for the end of submit process and return ResponseEntity
     * @return HTTP Response
     */
    public ResponseEntity<CommandResponseData> getResponse(){
        while(response == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
