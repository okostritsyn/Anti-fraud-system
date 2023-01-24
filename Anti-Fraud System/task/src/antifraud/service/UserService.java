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
        var listOfUsers = getListOfUsers();

        if (listOfUsers.size()==0) {
            user.addRole(Role.ADMINISTRATOR);
            user.setActive(true);
        }else{
            user.addRole(Role.MERCHANT);
            user.setActive(false);
        }

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
        var rolesSet = userEntity.getRoles();
        Role currRole = null;
        if (!rolesSet.isEmpty()) currRole = rolesSet.stream().findFirst().get();
        return new UserResultResponse(userEntity.getId(),userEntity.getName(),userEntity.getUsername(),currRole);
    }

    public Role getRoleByName(String role) {
        try{
            return Role.valueOf(role);
        } catch (IllegalArgumentException ex){
            log.error("Try to set role "+role+" which is no exist!",ex);
            return null;
        }
    }

    public void addRoleForUser(User user, Role currRole) {
        user.addRole(currRole);
        userRepository.save(user);
    }

    public void setActiveStatusUser(User user,boolean status) {
        user.setActive(status);
        userRepository.save(user);
    }
}
