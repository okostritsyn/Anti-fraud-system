package antifraud.controller;

import antifraud.model.User;
import antifraud.model.request.TransactionRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.*;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;
    UserService userService;

    @PostMapping(value ="/api/antifraud/transaction")
    @PreAuthorize("hasRole('USER')")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req){
        var result = transactionService.processTransaction(req.getAmount());
        return new TransactionResultResponse(result);
    }

    @PostMapping(value ="/api/auth/user")
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

    @GetMapping(value ="/api/auth/list")
    @PreAuthorize("hasRole('USER')")
    List<UserResultResponse> getListOfUsers(){
        var usersList = userService.getListOfUsers();
        var listResponse =  new ArrayList<UserResultResponse>();

        for (User user:usersList) {
            var currUser = userService.mapUserToUserDTO(user);
            listResponse.add(currUser);
        }

        return listResponse;
    }

    @DeleteMapping(value ="/api/auth/user/{username}")
    @PreAuthorize("hasRole('USER')")
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
}
