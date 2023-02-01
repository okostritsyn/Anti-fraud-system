package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.enums.Role;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.UserResultResponse;

public class UserMapper {
    public static User mapUserDTOToEntity(UserCreationRequest userDTO){
        var user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public static UserResultResponse mapUserToUserDTO(User userEntity){
        var rolesSet = userEntity.getRoles();
        Role currRole = null;
        if (!rolesSet.isEmpty()) currRole = rolesSet.stream().findFirst().get();
        return new UserResultResponse(userEntity.getId(),userEntity.getName(),userEntity.getUsername(),currRole);
    }

}
