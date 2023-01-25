package antifraud.controller;

import antifraud.model.Role;
import antifraud.model.User;
import antifraud.model.request.UserAccessRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.request.UserRoleSetRequest;
import antifraud.model.response.UserDeleteResponse;
import antifraud.model.response.UserDeleteStatus;
import antifraud.model.response.UserResultResponse;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    UserService userService;

    @PostMapping(value ="/user")
    @ResponseStatus(HttpStatus.CREATED)
    UserResultResponse createUser(@RequestBody @Valid UserCreationRequest user){
        var userEntity = userService.mapUserDTOToEntity(user);
        var status = userService.createUser(userEntity);
        if (!status){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User already exist!");
        }
        return userService.mapUserToUserDTO(userEntity);
    }

    @GetMapping(value ="/list")
    List<UserResultResponse> getListOfUsers(){
        var usersList = userService.getListOfUsers();
        var listResponse =  new ArrayList<UserResultResponse>();

        for (User user:usersList) {
            var currUser = userService.mapUserToUserDTO(user);
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
        var currRole = userService.getRoleByName(userRole.getRole());

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

        return userService.mapUserToUserDTO(user);
    }

    @PutMapping(value ="/access")
    Map<String,String> giveAccessUser(@RequestBody @Valid UserAccessRequest userAccess){
        if (!userAccess.getOperation().equals("LOCK") && !userAccess.getOperation().equals("UNLOCK")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong input data format!");
        }

        var username = userAccess.getUsername();
        var user = userService.findByName(username);

        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found!");
        }

        var status = userAccess.getOperation().equals("UNLOCK");
        var currStatusStr = status?"unlocked":"locked";
        userService.setActiveStatusUser(user,status);

        var message = "User "+userAccess.getUsername()+" "+currStatusStr+"!";
        var mapAnswer = new HashMap<String,String>();
        mapAnswer.put("status",message);
        return mapAnswer;
    }
}
