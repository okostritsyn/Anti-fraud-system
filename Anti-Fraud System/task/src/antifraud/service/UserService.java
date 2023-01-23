package antifraud.service;

import antifraud.model.Role;
import antifraud.model.User;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.UserResultResponse;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(User user){
        if (userRepository.findByName(user.getUsername()) != null) {
            return false;
        }
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
        log.info("Create user with name "+user.getUsername());
        return true;
    }

    public List<User> getListOfUsers(){
        return userRepository.findAll();
    }

    public User findByName(String name){
        return userRepository.findByName(name);
    }

    public boolean deleteUser(User user){
        boolean status = false;
        try {
            userRepository.delete(user);
            status = true;
        }catch (OptimisticLockingFailureException ex) {
            log.error("An error while delete user named "+user.getUsername(),ex);
        }
        return status;
    }

    public User mapUserDTOToEntity(UserCreationRequest UserDTO){
        var user = new User();
        user.setUsername(UserDTO.getUsername());
        user.setName(UserDTO.getName());
        user.setPassword(passwordEncoder.encode(UserDTO.getPassword()));

        return user;
    }

    public UserResultResponse mapUserToUserDTO(User userEntity){
        return new UserResultResponse(userEntity.getId(),userEntity.getName(),userEntity.getUsername());
    }
}
