package net.Broken.RestApi;


import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PlaylistRepository;
import net.Broken.DB.Repository.TrackRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.RestApi.Data.Playlist.AddToPlaylistData;
import net.Broken.RestApi.Data.Playlist.CreatePlaylistData;
import net.Broken.RestApi.Data.Playlist.DeleteTrackData;
import net.Broken.RestApi.Data.Playlist.PlaylistResponseData;
import net.Broken.audio.Playlist.PlaylistManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/playlist/")
public class PlaylistAPIController {

    private final
    UserRepository userRepository;

    private final
    PlaylistRepository playlistRepository;

    private final
    TrackRepository trackRepository;

    private Logger logger = LogManager.getLogger();

    @Autowired
    public PlaylistAPIController(UserRepository userRepository, PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }


    @RequestMapping("/myPlaylist")
    public List<PlaylistEntity> myPlaylist(@CookieValue(value = "token", defaultValue = "") String token){
        if(token.isEmpty())
            return null;
        else{
            UserEntity user = userRepository.findByApiToken(token).get(0);
            return  user.getPlaylists();
        }

    }

    @RequestMapping("/createPlaylist")
    public ResponseEntity<PlaylistResponseData> createPlaylist(@CookieValue(value = "token", defaultValue = "") String token, @RequestBody CreatePlaylistData data){

        if(token.isEmpty())
            return new ResponseEntity<>(new PlaylistResponseData("Unknown Token!\nPlease Re-connect.", "token"), HttpStatus.UNAUTHORIZED);
        else{
            UserEntity user = userRepository.findByApiToken(token).get(0);
            PlaylistEntity playlistEntity = new PlaylistEntity(data.name, user);
            playlistEntity = playlistRepository.save(playlistEntity);
            user.addPlaylist(playlistEntity);
            userRepository.save(user);
            return new ResponseEntity<>(new PlaylistResponseData("Ok", playlistEntity), HttpStatus.OK);
        }


    }

    @RequestMapping("/addToPlaylist")
    public ResponseEntity<PlaylistResponseData> addToPlaylist(@CookieValue(value = "token", defaultValue = "") String token, @RequestBody AddToPlaylistData data){
        PlaylistManager playlistManager = PlaylistManager.getINSTANCE();

        return playlistManager.addToPlaylist(token, data);

    }

    @RequestMapping("/dellTrack")
    public ResponseEntity<PlaylistResponseData> dellTrack(@CookieValue(value = "token", defaultValue = "") String token, @RequestBody DeleteTrackData data){
        PlaylistManager playlistManager = PlaylistManager.getINSTANCE();

        return playlistManager.removeTrack(token, data);

    }


}
