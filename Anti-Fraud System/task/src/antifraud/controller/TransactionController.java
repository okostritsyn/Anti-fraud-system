package antifraud.controller;

import antifraud.model.request.TransactionRequest;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.TransactionResultResponse;
import antifraud.model.response.UserCreationResultResponse;
import antifraud.model.response.UserDeleteResponse;
import antifraud.service.AuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransactionService transactionService;
    AuthorizationService authorizationService;

    @PostMapping(value ="/api/antifraud/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req){
        var result = transactionService.processTransaction(req.getAmount());
        return new TransactionResultResponse(result);
    }

    @PostMapping(value ="/api/auth/user")
    UserCreationResultResponse transaction(@RequestBody @Valid UserCreationRequest user){
        var result = authorizationService.createUser(user);
        //return new TransactionResultResponse(result);
    }

    @GetMapping(value ="/api/auth/list")
    List<UserCreationResultResponse> transaction(){
        var result = authorizationService.createUser(user);
        //return new TransactionResultResponse(result);
    }

    @DeleteMapping(value ="/api/auth/user")
    UserDeleteResponse transaction(@RequestParam @Valid String user){
        var result = authorizationService.createUser(user);
        //return new TransactionResultResponse(result);
    }
}
