package net.Broken.Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.MainBot;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingDeque<UserAudioTrack> queue;
    private final Guild guild;

    private UserAudioTrack currentPlayingTrack;
    private final Logger logger = LogManager.getLogger();

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        player.setVolume(25);
        this.queue = new LinkedBlockingDeque<>();
        this.currentPlayingTrack = null;
        this.guild = guild;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(UserAudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (track.getSubmittedUser() != MainBot.jda.getSelfUser()) {
            logger.debug("[" + guild + "] Flush history");
        }

        if (!player.startTrack(track.getAudioTrack(), true)) {
            queue.offer(track);
        } else {
            currentPlayingTrack = track;
        }
    }

    /**
     * Add track on top of playlist
     *
     * @param track
     */
    public void addNext(UserAudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track.getAudioTrack(), true)) {
            queue.addFirst(track);
        } else {
            currentPlayingTrack = track;
        }
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);

    }

    public void stop() {
        player.stopTrack();
        this.currentPlayingTrack = null;
        player.destroy();
    }

    public void flush() {
        queue.clear();
    }

    public List<UserAudioTrack> getList() {
        List<UserAudioTrack> temp = new ArrayList<>();
        Object[] test = queue.toArray();
        for (Object track : test) {
            UserAudioTrack casted = (UserAudioTrack) track;
            temp.add(casted);
        }
        return temp;
    }

    public AudioTrackInfo getInfo() {
        return player.getPlayingTrack().getInfo();
    }

    public UserAudioTrack getCurrentPlayingTrack() {
        return currentPlayingTrack;
    }

    public boolean remove(String uri) {
        for (UserAudioTrack track : queue) {
            if (track.getAudioTrack().getInfo().uri.equals(uri)) {
                if (!queue.remove(track)) {
                    logger.error("[" + guild + "] Delete failure!");
                    return false;
                } else {
                    logger.info("[" + guild + "] Delete successful");
                    return true;
                }
            }
        }
        logger.info("[" + guild + "] Delete failure! Not found.");

        return false;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        UserAudioTrack track = queue.poll();
        if (track != null){
            this.currentPlayingTrack = track;
            player.startTrack(track.getAudioTrack(), false);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if(queue.isEmpty()){
                logger.debug("[" + guild.getName() + "] End of track, Playlist empty.");
                GuildAudioWrapper.getInstance(guild).updateLastButton();
            }else{
                logger.debug("[" + guild.getName() + "] End of track, start next.");
                nextTrack();
            }


        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);
        GuildAudioWrapper.getInstance(guild).updateLastButton();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);
        GuildAudioWrapper.getInstance(guild).updateLastButton();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);
        GuildAudioWrapper.getInstance(guild).updateLastButton();
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
        GuildAudioWrapper.getInstance(guild).updateLastButton();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
        GuildAudioWrapper.getInstance(guild).updateLastButton();
    }
}
