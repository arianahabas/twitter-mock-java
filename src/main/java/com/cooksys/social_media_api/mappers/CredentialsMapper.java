package com.cooksys.social_media_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
	Credentials dtoToEntity(CredentialsDto credentialsDto);
	
	CredentialsDto entityToDto(Credentials credentials);
	
	List<Credentials> dtosToEntities(List<CredentialsDto> credentialsDtos);
	
	List<CredentialsDto> entitiesToDto(List<Credentials> credentials);
}
