package net.Broken.audio.Playlist;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PlaylistRepository;
import net.Broken.DB.Repository.TrackRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import net.Broken.RestApi.Data.Playlist.AddToPlaylistData;
import net.Broken.RestApi.Data.Playlist.DeleteTrackData;
import net.Broken.RestApi.Data.Playlist.MoveTrackData;
import net.Broken.RestApi.Data.Playlist.PlaylistResponseData;
import net.Broken.SpringContext;
import net.Broken.Tools.UserManager.Exceptions.UnknownTokenException;
import net.Broken.Tools.UserManager.UserUtils;
import net.Broken.audio.Playlist.Exception.PlaylistNotFoundException;
import net.Broken.audio.WebLoadUtils;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PlaylistManager {

    private final ResponseEntity<PlaylistResponseData> TOKEN_ERROR = new ResponseEntity<>(new PlaylistResponseData("Unknown Token!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);

    private final ResponseEntity<PlaylistResponseData> PLAYLIST_NOT_FOUND = new ResponseEntity<>(new PlaylistResponseData("Playlist not found", "playlist"), HttpStatus.NOT_FOUND);

    private final ResponseEntity<PlaylistResponseData> TRACK_NOT_FOUND = new ResponseEntity<>(new PlaylistResponseData("Can't find media!", "track"), HttpStatus.NOT_FOUND);



    private PlaylistRepository playlistRepository;

    private TrackRepository trackRepository;

    private UserRepository userRepository;

    Logger logger = LogManager.getLogger();

    private static PlaylistManager INSTANCE = new PlaylistManager();

    private PlaylistManager() {
        ApplicationContext context = SpringContext.getAppContext();

        playlistRepository = (PlaylistRepository) context.getBean("playlistRepository");
        trackRepository = (TrackRepository) context.getBean("trackRepository");
        userRepository = (UserRepository) context.getBean("userRepository");
    }

    public static PlaylistManager getINSTANCE() {
        return INSTANCE;
    }

    public ResponseEntity<PlaylistResponseData> addToPlaylist(String token, AddToPlaylistData data) {
        UserUtils userUtils = UserUtils.getInstance();

        try {
            UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
            PlaylistEntity playlist = getPlaylist(data.playlistId);

            User jdaUser = MainBot.jda.getUserById(user.getJdaId());

            WebLoadUtils webLoadUtils = new WebLoadUtils(data, jdaUser, MainBot.jda.getGuilds().get(0) , false);
            webLoadUtils.getResponse();

            if(webLoadUtils.userAudioTrack == null){
                return TRACK_NOT_FOUND;
            }
            else
            {
                TrackEntity trackEntity = new TrackEntity(webLoadUtils.userAudioTrack.getAudioTrack().getInfo(), data.pos, playlist);

                playlist = insert(playlist, trackEntity);
                return new ResponseEntity<>(new PlaylistResponseData("Ok", playlist),HttpStatus.OK);
            }



        } catch (UnknownTokenException e) {
            logger.warn("Unknown token: "+ token);
            return TOKEN_ERROR;
        } catch (PlaylistNotFoundException e) {
            logger.debug("Playlist not found: "+ data.playlistId);
            return PLAYLIST_NOT_FOUND;
        }
    }


    public ResponseEntity<PlaylistResponseData> removeTrack(String token, DeleteTrackData data){
        UserUtils userUtils = UserUtils.getInstance();
        try {
            UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
            PlaylistEntity playlist = getPlaylist(data.playlistId);



            TrackEntity toDelete = trackRepository.findOne(data.id);

            playlist = remove(playlist, toDelete);

            if(playlist == null)
            {
                logger.warn("Playlist: " + data.playlistId + " Track: " + data.id);
                return TRACK_NOT_FOUND;
            }

            return new ResponseEntity<>(new PlaylistResponseData("Ok", playlist),HttpStatus.OK);

        } catch (UnknownTokenException e) {
            logger.warn("Unknown token: "+ token);
            return TOKEN_ERROR;
        } catch (PlaylistNotFoundException e) {
            logger.debug("Playlist not found: "+ data.playlistId);
            return PLAYLIST_NOT_FOUND;
        }
    }

    public ResponseEntity<PlaylistResponseData> moveTrack(String token, MoveTrackData data) {
        UserUtils userUtils = UserUtils.getInstance();
        try {
            UserEntity user = userUtils.getUserWithApiToken(userRepository, token);
            PlaylistEntity playlist = getPlaylist(data.playlistId);



            TrackEntity toMove = trackRepository.findOne(data.id);

            TrackEntity save = new TrackEntity(toMove);

            playlist = remove(playlist, toMove);

            if(playlist == null)
            {
                logger.warn("Playlist: " + data.playlistId + " Track: " + data.id);
                return TRACK_NOT_FOUND;
            }

            save.setPos(data.newPos);

            playlist = insert(playlist, save);



            return new ResponseEntity<>(new PlaylistResponseData("Ok", playlist),HttpStatus.OK);

        } catch (UnknownTokenException e) {
            logger.warn("Unknown token: "+ token);
            return TOKEN_ERROR;
        } catch (PlaylistNotFoundException e) {
            logger.debug("Playlist not found: "+ data.playlistId);
            return PLAYLIST_NOT_FOUND;
        }
    }

    private PlaylistEntity getPlaylist(int id) throws PlaylistNotFoundException{
        PlaylistEntity playlist = playlistRepository.findOne(id);
        if(playlist == null)
            throw new PlaylistNotFoundException();
        else
            return playlist;

    }


    private PlaylistEntity insert(PlaylistEntity playlistEntity, TrackEntity trackEntity){
        List<TrackEntity> tracks = trackRepository.findDistinctByPlaylistOrderByPos(playlistEntity);


        boolean increase = false;
        for(TrackEntity track : tracks){
            if(track.getPos().equals(trackEntity.getPos())){
                logger.debug("Need re-organisation");
                increase = true;
            }


            if(increase){
                track.setPos(track.getPos() + 1);
                trackRepository.save(track);
            }
        }

        if(!increase)
        {
            trackEntity.setPos(tracks.size());
        }

        trackRepository.save(trackEntity);

        playlistEntity.addTracks(trackEntity);


        return playlistRepository.save(playlistEntity);

    }

    private PlaylistEntity remove(PlaylistEntity playlistEntity, TrackEntity trackEntity){

        if(trackEntity == null){
            logger.warn("Track not found in DB!");
            return null;
        }

        List<TrackEntity> tracks = trackRepository.findDistinctByPlaylistOrderByPos(playlistEntity);

        int toDeleteIndex = tracks.indexOf(trackEntity);
        logger.debug("To delete index: "  + toDeleteIndex);
        if(toDeleteIndex == -1){
            logger.warn("Track not found in playlist");
            return null;
        }


        for(int i = toDeleteIndex + 1; i< tracks.size(); i++){
            tracks.get(i).setPos(tracks.get(i).getPos() - 1);
            trackRepository.save(tracks.get(i));
        }

        tracks.remove(trackEntity);
        trackRepository.delete(trackEntity);

        playlistEntity.setTracks(tracks);
        playlistEntity = playlistRepository.save(playlistEntity);

        return playlistEntity;

    }


}
