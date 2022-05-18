package net.Broken.Api.Security;

import net.Broken.Api.Security.Services.UnauthorizedHandler;
import net.Broken.DB.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UnauthorizedHandler unauthorizedHandler;
    public SecurityConfig(UnauthorizedHandler unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                // Our private endpoints
                .antMatchers("/api/v2/**").permitAll()
                .anyRequest().permitAll();
//        http.authenticationProvider(discordAuthenticationProvider);

//        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//        });
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
