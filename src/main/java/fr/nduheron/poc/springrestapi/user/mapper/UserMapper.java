package fr.nduheron.poc.springrestapi.user.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.nduheron.poc.springrestapi.user.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.user.dto.UserDto;
import fr.nduheron.poc.springrestapi.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(User user);

	User toEntity(CreateUserDto user);

	User toEntity(UpdateUserDto user);

	List<UserDto> toDto(List<User> users);

}
