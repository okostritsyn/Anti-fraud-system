package antifraud.service;

import antifraud.exception.BusinessException;
import antifraud.exception.ConflictRegisterEntityException;
import antifraud.exception.EntityNotExist;
import antifraud.exception.ValidationDTOFailedException;
import antifraud.mapper.UserMapper;
import antifraud.model.enums.Role;
import antifraud.model.User;
import antifraud.model.enums.UserAccessOperation;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.UserResultResponse;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest user){
        if (userRepository.findByName(user.getUsername()) != null) {
            throw new ConflictRegisterEntityException("User already exist!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var userEntity = UserMapper.mapUserDTOToEntity(user);
        registerUser(userEntity);
        return userEntity;
    }

    private void registerUser(User user){
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
    }

    public List<UserResultResponse> getListOfUsers(){
        var usersList = userRepository.findAll();
        var listResponse =  new ArrayList<UserResultResponse>();

        for (User user:usersList) {
            var currUser = UserMapper.mapUserToUserDTO(user);
            listResponse.add(currUser);
        }

        return listResponse;
    }

    public User findByName(String name){
        return userRepository.findByName(name);
    }

    private boolean deleteUser(User user){
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

    private void checkUserBeforeChangeRole(String username, Role currRole) {
        if (currRole == Role.INVALID) {
            log.error("Try to set role "+currRole+" which is no exist!");
            throw new ValidationDTOFailedException("Try to set role "+currRole+" which is no exist!");
        }
        if (currRole != Role.MERCHANT && currRole != Role.SUPPORT){
            throw new ValidationDTOFailedException("Wrong role!");
        }
        var user = findByName(username);
        if (user == null){
            throw new EntityNotExist("User not found!");
        }

        if (checkUserRoleConflict(user,currRole)){
            throw new ConflictRegisterEntityException("Conflict while add new role: role already exist");
        }
    }

    private boolean checkUserRoleConflict(User user, Role currRole) {
        return user != null && user.getRoles().contains(currRole);
    }

    private void checkUserBeforeChangeAccess(String username, UserAccessOperation operation) {
        if (operation == UserAccessOperation.INVALID){
            throw new ValidationDTOFailedException("Wrong input data format!");
        }

        var user = findByName(username);
        if (user == null){
            throw new EntityNotExist("User not found!");
        }

        if (getRoleForUser(user) == Role.ADMINISTRATOR){
            throw new BusinessException("Cannot change access for ADMINISTRATOR");
        }
    }

    public void giveAccessUser(String username, UserAccessOperation operation) {
        checkUserBeforeChangeAccess(username,operation);

        var user = findByName(username);
        if (user != null){
            var status = operation.equals(UserAccessOperation.UNLOCK);
            setActiveStatusUser(user,status);
        }
    }

    public void deleteUserByName(String username) {
        var user = findByName(username);
        boolean status = false;
        if (user != null) status = deleteUser(user);
        if (!status){
            throw new EntityNotExist("User not found!");
        }
    }

    public User setRoleForUser(String username, Role role) {
        checkUserBeforeChangeRole(username, role);
        var user = findByName(username);
        if (user == null) {
            throw new EntityNotExist("User not found!");
        }
        addRoleForUser(user,role);
        return user;
    }
}
