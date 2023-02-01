package antifraud.service;

import antifraud.model.enums.Role;
import antifraud.model.User;
import antifraud.model.enums.UserAccessOperation;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<HttpStatus,String> checkUserBeforeChangeRole(String username, Role currRole) {
        var mapOfErrors = new HashMap<HttpStatus,String>();
        if (currRole == Role.INVALID) {
            log.error("Try to set role "+currRole+" which is no exist!");
            mapOfErrors.put(HttpStatus.BAD_REQUEST,"Try to set role "+currRole+" which is no exist!");
        }
        if (currRole != Role.MERCHANT && currRole != Role.SUPPORT){
            mapOfErrors.put(HttpStatus.BAD_REQUEST,"Wrong role!");
        }
        var user = findByName(username);
        if (user == null){
            mapOfErrors.put(HttpStatus.NOT_FOUND,"User not found!");
        }

        if (checkUserRoleConflict(user,currRole)){
            mapOfErrors.put(HttpStatus.CONFLICT,"Conflict while add new role: role already exist");
        }

        return mapOfErrors;
    }

    public boolean checkUserRoleConflict(User user, Role currRole) {
        return user != null && user.getRoles().contains(currRole);
    }

    public Map<HttpStatus,String> checkUserBeforeChangeAccess(String username, UserAccessOperation operation) {
        var mapOfErrors = new HashMap<HttpStatus,String>();
        if (operation == UserAccessOperation.INVALID){
            mapOfErrors.put(HttpStatus.BAD_REQUEST,"Wrong input data format!");
        }

        var user = findByName(username);
        if (user == null){
            mapOfErrors.put(HttpStatus.NOT_FOUND,"User not found!");
        }

        if (user != null && getRoleForUser(user) == Role.ADMINISTRATOR){
            mapOfErrors.put(HttpStatus.BAD_REQUEST,"Cannot change access for ADMINISTRATOR");
        }
        return mapOfErrors;
    }

    public void giveAccessUser(String username, UserAccessOperation operation) {
        var user = findByName(username);
        if (user != null){
            var status = operation.equals(UserAccessOperation.UNLOCK);
            setActiveStatusUser(user,status);
        }
    }
}
