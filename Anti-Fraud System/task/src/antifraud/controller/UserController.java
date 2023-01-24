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
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<?> createUser(@RequestBody @Valid UserCreationRequest user){
        var userEntity = userService.mapUserDTOToEntity(user);
        var status = userService.createUser(userEntity);
        if (status){
            var userResponse = userService.mapUserToUserDTO(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userResponse);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .build();
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
    ResponseEntity<?> deleteUsers(@PathVariable String username){
        var user = userService.findByName(username);
        boolean status = false;
        if (user != null) status = userService.deleteUser(user);

        var userDeleteResponse = new UserDeleteResponse(username, UserDeleteStatus.SUCCESS);
        if (status){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userDeleteResponse);
        }
        userDeleteResponse.setStatus(UserDeleteStatus.USER_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(userDeleteResponse);
    }

    @PutMapping(value ="/role")
    ResponseEntity<?> SetRoleForUser(@RequestBody @Valid UserRoleSetRequest userRole){
        var currRole = userService.getRoleByName(userRole.getRole());

        if (currRole != Role.MERCHANT && currRole != Role.SUPPORT){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        var username = userRole.getUsername();
        var user = userService.findByName(username);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (user.getRoles().contains(currRole)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }

        userService.addRoleForUser(user,currRole);

        var userResponse = userService.mapUserToUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userResponse);

    }

    @PutMapping(value ="/access")
    Map<String,String> giveAccessUser(@RequestBody @Valid UserAccessRequest userAccess){
        if (!userAccess.getOperation().equals("LOCK") && !userAccess.getOperation().equals("UNLOCK")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var username = userAccess.getUsername();
        var user = userService.findByName(username);

        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var status = userAccess.getOperation().equals("UNLOCK");
        var currStatusStr = userAccess.getOperation().equals("LOCK")?"locked":"unlocked";
        userService.setActiveStatusUser(user,status);

        var message = "User "+userAccess.getUsername()+" "+currStatusStr+"!";
        var mapAnswer = new HashMap<String,String>();
        mapAnswer.put("status",message);
        return mapAnswer;
    }
}
