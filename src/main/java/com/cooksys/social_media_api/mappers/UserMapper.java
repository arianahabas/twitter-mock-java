package com.cooksys.social_media_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.User;

@Mapper(componentModel = "spring", uses = { CredentialsMapper.class, ProfileMapper.class })
public interface UserMapper {

	User responseDtoToEntity(UserResponseDto userResponseDto);
	
	User requestDtoToEntity(UserRequestDto userRequestDto);

	UserRequestDto entityToRequestDto(User user);

	@Mapping(target = "username", source = "credentials.username")
	UserResponseDto entityToResponseDto(User user);
	
	List<UserRequestDto> entitiesToRequestDtos(List<User> user);
	
	List<UserResponseDto> entitiesToResponseDtos(List<User> user);
	
	List<User> requestDtosToEntities(List<UserRequestDto> requestDtos);
	
	List<User> responseDtosToEntities(List<UserResponseDto> responseDtos);

}
