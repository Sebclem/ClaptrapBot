package net.Broken.Api.Security.Filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.Broken.Api.Security.Data.JwtPrincipal;
import net.Broken.Api.Security.Services.JwtService;
import net.Broken.BotConfigLoader;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private BotConfigLoader config;
    @Autowired
    private UserRepository userRepository;
    private final Logger logger = LogManager.getLogger();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            try {
                UserEntity user;
                JwtPrincipal principal;
                if (config.mode().equals("DEV")) {
                    user = userRepository.findByDiscordId(token).orElseThrow();
                    principal = new JwtPrincipal("DEV", user);
                } else {
                    Jws<Claims> jwt = jwtService.verifyAndParseJwt(token);
                    user = jwtService.getUserWithJwt(jwt);
                    principal = new JwtPrincipal(jwt.getPayload().getId(), user);
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        principal, null, new ArrayList<>());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                logger.warn("[JWT] Cannot set user authentication: " + e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
