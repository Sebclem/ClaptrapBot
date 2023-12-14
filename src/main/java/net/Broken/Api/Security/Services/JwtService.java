package net.Broken.Api.Security.Services;

import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;

@Service
public class JwtService {

    private final SecretKey jwtKey;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtKey = Jwts.SIG.HS256.key().build();
    }

    public String buildJwt(UserEntity user) {
        Date iat = new Date();
        Date nbf = new Date();
        Calendar expCal = Calendar.getInstance();
        expCal.add(Calendar.DATE, 7);
        Date exp = expCal.getTime();
        UUID uuid = UUID.randomUUID();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("discord_id", user.getDiscordId())
                .claim("avatar", user.getAvatar())
                .claim("discriminator", user.getDiscriminator())
                .id(uuid.toString())
                .issuedAt(iat)
                .notBefore(nbf)
                .expiration(exp)
                .signWith(this.jwtKey)
                .compact();

    }

    public Jws<Claims> verifyAndParseJwt(String token) {
        return Jwts.parser()
                .verifyWith(this.jwtKey)
                .build()
                .parseSignedClaims(token);
    }

    public UserEntity getUserWithJwt(Jws<Claims> jwt) throws NoSuchElementException {
        String discordId = jwt.getPayload().get("discord_id", String.class);
        return userRepository.findByDiscordId(discordId)
                .orElseThrow();
    }
}
