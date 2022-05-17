package net.Broken.Api.Security;

import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public DiscordUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserEntity> user = userRepository.findByName(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException(username);
        }
        return new DiscordUserPrincipal(user.get(0));
    }
}
