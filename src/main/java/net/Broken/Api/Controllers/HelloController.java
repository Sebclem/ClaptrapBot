package net.Broken.Api.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.DB.Entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/hello")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HelloController {


    @GetMapping("world")
    public String helloWorld(Authentication authentication){
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return "Hello " + principal.user().getUsername();
    }
}
