package antifraud.mapper;

import antifraud.model.User;
import antifraud.model.response.UserResultResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = CustomAbstractMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CustomUserListMapper {
    public abstract List<UserResultResponse> mapUsersToUsersDTO(List<User> userList);

}
