package net.Broken.Api.Security.Services;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private final Key jwtKey;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String buildJwt(UserEntity user){
        Date iat = new Date();
        Date nbf = new Date();
        Calendar expCal = Calendar.getInstance();
        expCal.add(Calendar.DATE, 7);
        Date exp = expCal.getTime();
        UUID uuid = UUID.randomUUID();


        return Jwts.builder()
                .setSubject(user.getName())
                .claim("discord_id", user.getDiscordId())
                .setId(uuid.toString())
                .setIssuedAt(iat)
                .setNotBefore(nbf)
                .setExpiration(exp)
                .signWith(this.jwtKey)
                .compact();


    }


    public Jws<Claims> verifyAndParseJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.jwtKey)
                .build()
                .parseClaimsJws(token);
    }


    public UserEntity getUserWithJwt(Jws<Claims> jwt) throws NoSuchElementException {
        String discordId = jwt.getBody().get("discord_id", String.class);
        return userRepository.findByDiscordId(discordId)
                .orElseThrow();
    }
}
