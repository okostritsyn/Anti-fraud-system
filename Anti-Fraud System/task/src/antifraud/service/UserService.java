package antifraud.service;

import antifraud.model.User;
import antifraud.model.enums.Role;
import antifraud.model.enums.UserAccessOperation;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.UserResultResponse;

import java.util.ArrayList;
import java.util.List;

public interface UserService {
    User createUser(UserCreationRequest user);

    void giveAccessUser(String username, UserAccessOperation operation);

    void deleteUserByName(String username);

    User setRoleForUser(String username, Role role);

    List<UserResultResponse> getListOfUsers();
}
