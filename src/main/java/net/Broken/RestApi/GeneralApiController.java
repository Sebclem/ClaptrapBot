package net.Broken.RestApi;

import net.Broken.DB.Entity.PlaylistEntity;
import net.Broken.DB.Entity.TrackEntity;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.PlaylistRepository;
import net.Broken.DB.Repository.TrackRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.MainBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class GeneralApiController {

    private final
    UserRepository userRepository;

    private final
    PlaylistRepository playlistRepository;

    private final
    TrackRepository trackRepository;

    @Autowired
    public GeneralApiController(UserRepository userRepository, PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }

    @RequestMapping(value = "/isReady", method = RequestMethod.GET)
    public ResponseEntity<String> isReady(){
        if(MainBot.ready){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
