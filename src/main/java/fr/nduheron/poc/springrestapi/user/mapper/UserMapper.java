package fr.nduheron.poc.springrestapi.user.mapper;

import java.util.List;

import fr.nduheron.poc.springrestapi.dto.CreateUserDto;
import fr.nduheron.poc.springrestapi.dto.UpdateUserDto;
import fr.nduheron.poc.springrestapi.dto.UserDto;
import org.mapstruct.Mapper;


import fr.nduheron.poc.springrestapi.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDto toDto(User user);

	User toEntity(CreateUserDto user);

	User toEntity(UpdateUserDto user);

	List<UserDto> toDto(List<User> users);

}
