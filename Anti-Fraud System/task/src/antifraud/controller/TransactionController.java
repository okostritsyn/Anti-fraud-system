package antifraud.controller;

import antifraud.model.request.TransactionRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.TransactionResultResponse;
import antifraud.model.response.UserCreationResultResponse;
import antifraud.model.response.UserDeleteResponse;
import antifraud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;
    UserService userService;

    @PostMapping(value ="/api/antifraud/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req){
        var result = transactionService.processTransaction(req.getAmount());
        return new TransactionResultResponse(result);
    }

    @PostMapping(value ="/api/auth/user")
    ResponseEntity<?> createUser(@RequestBody @Valid UserCreationRequest user){
        var userEntity = userService.mapUserDTOToEntity(user);
        var status = userService.createUser(userEntity);
        if (status){
            var userResponse = new UserCreationResultResponse(userEntity.getId(),userEntity.getName(),userEntity.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userResponse);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .build();
    }

    @GetMapping(value ="/api/auth/list")
    List<UserCreationResultResponse> getListOfUsers(){
        return null;
    }

    @DeleteMapping(value ="/api/auth/user/{username}")
    UserDeleteResponse deleteUsers(@PathVariable String username){
      return null;
    }
}
