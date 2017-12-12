package net.Broken.RestApi;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.Broken.Commandes.Music;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.CurrentMusicData;
import net.Broken.audio.NotConectedException;
import net.Broken.audio.NullMusicManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music/")
public class MusicWebController {
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
}
