package com.cooksys.social_media_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.dtos.ProfileDto;


@Mapper(componentModel = "spring")

public interface ProfileMapper {
	Profile dtoToEntity(ProfileDto profileDto);

	ProfileDto entitiyToDto(Profile profile);


}
