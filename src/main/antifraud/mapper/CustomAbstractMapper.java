package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.response.UserResultResponse;
import antifraud.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CustomAbstractMapper {
    @Autowired
    protected UserService userService;

    @Mapping(target = "role", expression = "java(userService.getRoleForUser(userEntity.getUsername()))")
    public abstract UserResultResponse mapUserToUserDTO(User userEntity);
}
