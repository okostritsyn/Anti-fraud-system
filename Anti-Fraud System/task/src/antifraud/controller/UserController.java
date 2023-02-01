package antifraud.controller;

import antifraud.mapper.UserMapper;
import antifraud.model.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

    @PostMapping(value ="/user")
    @ResponseStatus(HttpStatus.CREATED)
    UserResultResponse createUser(@RequestBody @Valid UserCreationRequest user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var userEntity = UserMapper.mapUserDTOToEntity(user);
        var status = userService.createUser(userEntity);
        if (!status){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User already exist!");
        }
        return UserMapper.mapUserToUserDTO(userEntity);
    }

    @GetMapping(value ="/list")
    List<UserResultResponse> getListOfUsers(){
        var usersList = userService.getListOfUsers();
        var listResponse =  new ArrayList<UserResultResponse>();

        for (User user:usersList) {
            var currUser = UserMapper.mapUserToUserDTO(user);
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
        var mapOfErrors = userService.checkUserBeforeChangeRole(userRole.getUsername(),
                userRole.getRole());
        for (HttpStatus status:mapOfErrors.keySet()) {
            throw new ResponseStatusException(status,mapOfErrors.get(status));
        }

        var user = userService.findByName(userRole.getUsername());
        userService.addRoleForUser(user,currRole);
        return UserMapper.mapUserToUserDTO(user);
    }

    @PutMapping(value ="/access")
    Map<String,String> giveAccessUser(@RequestBody @Valid UserAccessRequest userAccess){
        var mapOfErrors = userService.checkUserBeforeChangeAccess(userAccess.getUsername(),
                userAccess.getOperation());
        for (HttpStatus status:mapOfErrors.keySet()) {
            throw new ResponseStatusException(status,mapOfErrors.get(status));
        }
        userService.giveAccessUser(userAccess.getUsername(),userAccess.getOperation());

        var currStatusStr = userAccess.getOperation().getMessage();
        var message = "User "+userAccess.getUsername()+" "+currStatusStr+"!";
        var mapAnswer = new HashMap<String,String>();
        mapAnswer.put("status",message);
        return mapAnswer;
    }
}
