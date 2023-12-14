package net.Broken.Api.Controllers;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@CrossOrigin(origins = "*", maxAge = 3600)
@Hidden
public class CrossOptionController {

    /**
     * For cross preflight request send by axios
     */
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> handle() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
