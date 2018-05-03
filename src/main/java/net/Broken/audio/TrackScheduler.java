package net.Broken.audio;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.UserAudioTrackData;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.entities.Guild;
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
    private boolean autoFlow = false;
    private ArrayList<String> history = new ArrayList<>();
    private Logger logger = LogManager.getLogger();

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
        if(track.getSubmittedUser() != MainBot.jda.getSelfUser()){
            logger.debug("Flush history");
            history = new ArrayList<>();
        }

        history.add(track.getAudioTrack().getIdentifier());
        if (!player.startTrack(track.getAudioTrack(), true)) {
            queue.offer(track);
        }
        else{
            currentPlayingTrack = track;
        }
        if(track.getSubmittedUser() != MainBot.jda.getSelfUser()) {
            needAutoPlay();
        }


    }

    /**
     * Add track on top of playlist
     * @param track
     */
    public void addNext(UserAudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if(track.getSubmittedUser() != MainBot.jda.getSelfUser()){
            logger.debug("Flush history");
            history = new ArrayList<>();
        }

        history.add(track.getAudioTrack().getIdentifier());
        if (!player.startTrack(track.getAudioTrack(), true)) {
            queue.addFirst(track);
        }
        else{
            currentPlayingTrack = track;
        }
        if(track.getSubmittedUser() != MainBot.jda.getSelfUser()) {

            needAutoPlay();
        }
        else
            logger.debug("Bot add, ignore autoFlow");
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);

    }

    public void stop(){
        player.stopTrack();
        this.currentPlayingTrack = null;
        player.destroy();
    }

    public void flush(){
        queue.clear();
    }

    public List<UserAudioTrackData> getList(){
//        AudioTrack[] test = (AudioTrack[]) queue.toArray();

        List<UserAudioTrackData> temp = new ArrayList<>();
        Object[] test = queue.toArray();
        for(Object track: test){
            UserAudioTrack casted = (UserAudioTrack) track;
            temp.add(new UserAudioTrackData(casted.getSubmittedUser().getName(), casted.getAudioTrack().getInfo()));
        }
        return temp;
    }

    public AudioTrackInfo getInfo(){
        return player.getPlayingTrack().getInfo();
    }

    public UserAudioTrack getCurrentPlayingTrack() {
        return currentPlayingTrack;
    }

    public boolean remove(String uri){
        for(UserAudioTrack track : queue){
            if(track.getAudioTrack().getInfo().uri.equals(uri)){
                if(!queue.remove(track)) {
                    logger.error("Delete failure!");
                    return false;
                } else {
                    logger.info("Delete succeful");
                    needAutoPlay();
                    return true;
                }
            }
        }
        logger.info("Delete failure! Not found.");

        return false;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        UserAudioTrack track = queue.poll();
        if(track != null)
            this.currentPlayingTrack = track;
        if(track != null)
            player.startTrack(track.getAudioTrack(), false);
        needAutoPlay();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            logger.debug("End of track, start next.");
            nextTrack();
        }
    }

    private void needAutoPlay(){
        if((queue.size() < 1) && autoFlow && currentPlayingTrack != null){
            logger.debug("Auto add needed!");
            AudioM audioM = AudioM.getInstance(guild);
            YoutubeTools youtubeTools = YoutubeTools.getInstance();
            try {
                String id =  youtubeTools.getRelatedVideo(currentPlayingTrack.getAudioTrack().getInfo().identifier, history);
                logger.debug("Related id: "+id);
                audioM.loadAndPlayAuto(id);

            } catch (GoogleJsonResponseException e) {
                logger.error("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
            } catch (Throwable t) {
                logger.catching(t);
            }

        }
    }

    public void setAutoFlow(boolean autoFlow) {
        this.autoFlow = autoFlow;
        needAutoPlay();
    }

    public boolean isAutoFlow() {
        return autoFlow;
    }
}
