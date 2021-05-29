package fr.nduheron.poc.springrestapi.user.mapper;

import fr.nduheron.poc.springrestapi.user.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(User user);

	User toEntity(CreateUserDto user);

	List<UserDto> toDto(List<User> users);

}
