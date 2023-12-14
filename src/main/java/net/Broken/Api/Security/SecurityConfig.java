package net.Broken.Api.Security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.Broken.Api.Security.Components.UnauthorizedHandler;
import net.Broken.Api.Security.Filters.JwtFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final UnauthorizedHandler unauthorizedHandler;

    public SecurityConfig(UnauthorizedHandler unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults()).csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .antMatchers("/api/v2/auth/**").permitAll()
                        .antMatchers("/api/v2/guild/inviteLink").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/swagger-ui.html").permitAll()
                        .antMatchers("/v3/api-docs/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
}
