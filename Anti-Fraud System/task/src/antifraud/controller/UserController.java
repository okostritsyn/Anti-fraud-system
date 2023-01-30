package antifraud.controller;

import antifraud.mapper.UserMapper;
import antifraud.model.enums.Role;
import antifraud.model.User;
import antifraud.model.enums.UserAccessOperation;
import antifraud.model.request.UserAccessRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.request.UserRoleSetRequest;
import antifraud.model.response.UserDeleteResponse;
import antifraud.model.enums.UserDeleteStatus;
import antifraud.model.response.UserResultResponse;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    UserService userService;
    UserMapper userMapper;

    @PostMapping(value ="/user")
    @ResponseStatus(HttpStatus.CREATED)
    UserResultResponse createUser(@RequestBody @Valid UserCreationRequest user){
        var userEntity = userMapper.mapUserDTOToEntity(user);
        var status = userService.createUser(userEntity);
        if (!status){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User already exist!");
        }
        return userMapper.mapUserToUserDTO(userEntity);
    }

    @GetMapping(value ="/list")
    List<UserResultResponse> getListOfUsers(){
        var usersList = userService.getListOfUsers();
        var listResponse =  new ArrayList<UserResultResponse>();

        for (User user:usersList) {
            var currUser = userMapper.mapUserToUserDTO(user);
            listResponse.add(currUser);
        }

        return listResponse;
    }

    @DeleteMapping(value ="/user/{username}")
    UserDeleteResponse deleteUsers(@PathVariable String username){
        var user = userService.findByName(username);
        boolean status = false;
        if (user != null) status = userService.deleteUser(user);
        if (!status){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }
        return new UserDeleteResponse(username, UserDeleteStatus.SUCCESS);
    }

    @PutMapping(value ="/role")
    UserResultResponse SetRoleForUser(@RequestBody @Valid UserRoleSetRequest userRole){
        var currRole = userRole.getRole();

        if (currRole == Role.INVALID) log.error("Try to set role "+currRole+" which is no exist!");

        if (currRole != Role.MERCHANT && currRole != Role.SUPPORT){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var username = userRole.getUsername();
        var user = userService.findByName(username);

        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }

        if (user.getRoles().contains(currRole)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Conflict while add new role: role already exist");
        }

        userService.addRoleForUser(user,currRole);

        return userMapper.mapUserToUserDTO(user);
    }

    @PutMapping(value ="/access")
    Map<String,String> giveAccessUser(@RequestBody @Valid UserAccessRequest userAccess){
        if (userAccess.getOperation() == UserAccessOperation.INVALID){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong input data format!");
        }

        var username = userAccess.getUsername();
        var user = userService.findByName(username);

        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }

        if (userService.getRoleForUser(user) == Role.ADMINISTRATOR){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot change access for ADMINISTRATOR");
        }

        var status = userAccess.getOperation().equals(UserAccessOperation.UNLOCK);
        var currStatusStr = userAccess.getOperation().getMessage();
        userService.setActiveStatusUser(user,status);

        var message = "User "+userAccess.getUsername()+" "+currStatusStr+"!";
        var mapAnswer = new HashMap<String,String>();
        mapAnswer.put("status",message);
        return mapAnswer;
    }
}
