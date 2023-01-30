package antifraud.service;

import antifraud.model.enums.Role;
import antifraud.model.User;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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

    public void addRoleForUser(User user, Role currRole) {
        user.addRole(currRole);
        userRepository.save(user);
    }

    public void setActiveStatusUser(User user,boolean status) {
        user.setActive(status);
        userRepository.save(user);
    }

    public Role getRoleForUser(User user) {
        var rolesSet = user.getRoles();
        return rolesSet.stream().findFirst().orElse(null);
    }
}
