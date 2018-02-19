package net.Broken.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.Broken.Commands.Music;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebLoadUtils {
    ResponseEntity<CommandResponseData> response;
    Logger logger = LogManager.getLogger();

    public WebLoadUtils(Music musicCommande, CommandPostData data, User user){
        AudioPlayerManager playerM = musicCommande.getAudioManager().getPlayerManager();
        try {

            AudioM audioM = musicCommande.getAudioManager();
            playerM.loadItemOrdered(musicCommande.getAudioManager().getMusicManager(), data.url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    logger.info("Single Track detected from web!");

                    try {
                        UserAudioTrack userAudioTrack = new UserAudioTrack(user, track); //TODO
                        audioM.play(audioM.getGuild(), audioM.getPlayedChanel(), audioM.getMusicManager(), userAudioTrack, data.onHead);
                        response = new ResponseEntity<>(new CommandResponseData("ADD", "Loaded"), HttpStatus.OK);
                    } catch (NullMusicManager | NotConectedException nullMusicManager) {
                        nullMusicManager.printStackTrace();
                    }

                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {

                    logger.info("Playlist detected from web! Limit: " + data.playlistLimit);
                    audioM.playListLoader(playlist, data.playlistLimit, user, data.onHead);
                    response = new ResponseEntity<>(new CommandResponseData("ADD", "Loaded"), HttpStatus.OK);

                }

                @Override
                public void noMatches() {
                    logger.warn("Cant find media ! (web)");
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

        } catch (NullMusicManager | NotConectedException | InterruptedException nullMusicManager) {
            nullMusicManager.printStackTrace();
        }
    }

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
