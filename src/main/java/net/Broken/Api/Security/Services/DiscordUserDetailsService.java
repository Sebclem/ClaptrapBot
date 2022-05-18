package net.Broken.Api.Security.Services;

import net.Broken.Api.Security.DiscordUserPrincipal;
import net.Broken.DB.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DiscordUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public DiscordUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new DiscordUserPrincipal(
                userRepository.findByJdaId(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username))
        );
    }
}
