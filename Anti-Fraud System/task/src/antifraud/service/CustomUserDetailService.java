package antifraud.service;

import antifraud.model.enums.Role;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("success authorization for " + username);
        var currUser = userRepository.findByName(username);
        if (currUser != null){
            var currRole = currUser.getRoles().stream().findFirst().orElse(Role.INVALID).name();
            log.info("success authorization for role " + currRole);
        }
        return currUser;
    }
}
