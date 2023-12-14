package net.Broken.Api.Controllers;

import net.Broken.Api.Data.Login;
import net.Broken.Api.Security.Data.JwtResponse;
import net.Broken.Api.Security.Services.JwtService;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/discord")
    public JwtResponse loginDiscord(@Validated @RequestBody Login login) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.redirectUri(), login.code()));

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String jwt = jwtService.buildJwt(user);

        return new JwtResponse(jwt);
    }
}
