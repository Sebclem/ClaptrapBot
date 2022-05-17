package net.Broken.Api.Security;

import net.Broken.DB.Repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                // Our private endpoints
                .anyRequest().authenticated();

//        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//        });

    }
}
