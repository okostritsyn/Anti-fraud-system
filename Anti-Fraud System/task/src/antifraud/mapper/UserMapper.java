package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.enums.Role;
import antifraud.model.request.UserCreationRequest;
import antifraud.model.response.UserResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User mapUserDTOToEntity(UserCreationRequest userDTO){
        var user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return user;
    }

    public UserResultResponse mapUserToUserDTO(User userEntity){
        var rolesSet = userEntity.getRoles();
        Role currRole = null;
        if (!rolesSet.isEmpty()) currRole = rolesSet.stream().findFirst().get();
        return new UserResultResponse(userEntity.getId(),userEntity.getName(),userEntity.getUsername(),currRole);
    }

}
