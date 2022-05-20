package net.Broken.Api.Security.Services;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.Broken.DB.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private final Key jwtKey;

    public JwtService() {
        this.jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String buildJwt(UserEntity user){
        Date iat = new Date();
        Date nbf = new Date();
        Calendar expCal = Calendar.getInstance();
        expCal.add(Calendar.DATE, 7);
        Date exp = expCal.getTime();


        return Jwts.builder()
                .setSubject(user.getName())
                .setId(user.getJdaId())
                .setIssuedAt(iat)
                .setNotBefore(nbf)
                .setExpiration(exp)
                .signWith(this.jwtKey)
                .compact();


    }
}
