package antifraud.controller;

import antifraud.mapper.UserMapper;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserController {
    UserService userService;

    @PostMapping(value ="/user")
    @ResponseStatus(HttpStatus.CREATED)
    UserResultResponse createUser(@RequestBody @Valid UserCreationRequest user){
        var userEntity = userService.createUser(user);
        return UserMapper.mapUserToUserDTO(userEntity);
    }

    @GetMapping(value ="/list")
    List<UserResultResponse> getListOfUsers(){
        return userService.getListOfUsers();
    }

    @DeleteMapping(value ="/user/{username}")
    UserDeleteResponse deleteUsers(@PathVariable String username){
        userService.deleteUserByName(username);
        return new UserDeleteResponse(username, UserDeleteStatus.SUCCESS);
    }

    @PutMapping(value ="/role")
    UserResultResponse setRoleForUser(@RequestBody @Valid UserRoleSetRequest userRole){
        var user = userService.setRoleForUser(userRole.getUsername(),userRole.getRole());
        return UserMapper.mapUserToUserDTO(user);
    }

    @PutMapping(value ="/access")
    Map<String,String> giveAccessUser(@RequestBody @Valid UserAccessRequest userAccess){
        userService.giveAccessUser(userAccess.getUsername(),userAccess.getOperation());

        var currStatusStr = userAccess.getOperation().getMessage();
        var message = "User "+userAccess.getUsername()+" "+currStatusStr+"!";
        var mapAnswer = new HashMap<String,String>();
        mapAnswer.put("status",message);
        return mapAnswer;
    }
}
