package com.cooksys.social_media_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.User;



@Mapper(componentModel = "spring", uses = { CredentialsMapper.class, ProfileMapper.class, TweetMapper.class })
public interface UserMapper {
	
	User requestDtoToEntity(UserRequestDto userRequestDto);
	
	User responseDtoToEntity(UserResponseDto userResponseDto);
	
	UserRequestDto entityToRequestDto(User user);
	
	UserResponseDto entityToResponseDto(User user);
	
	List<UserRequestDto> entitiesToRequestDtos(List<User> user);
	
	List<UserResponseDto> entitiesToResponseDtos(List<User> user);
	
	List<User> requestDtosToEntities(List<UserRequestDto> requestDtos);
	
	List<User> responseDtosToEntities(List<UserResponseDto> responseDtos);

}
