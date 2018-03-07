package net.Broken.RestApi;

import net.Broken.MainBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class GeneralApiController {
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
