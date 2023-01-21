package antifraud.service;

import antifraud.model.Role;
import antifraud.model.User;
import antifraud.model.request.UserCreationRequest;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean createUser(User user){
        if (userRepository.findByName(user.getName()) != null) {
            return false;
        }
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
        log.info("Create user with name "+user.getName());
        return true;
    }

    public User mapUserDTOToEntity(UserCreationRequest UserDTO){
        var user = new User();
        user.setUserName(UserDTO.getUserName());
        user.setName(UserDTO.getName());
        user.setPassword(UserDTO.getPassword());

        return user;
    }
}
