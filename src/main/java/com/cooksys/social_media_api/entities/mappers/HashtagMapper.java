package com.cooksys.social_media_api.entities.mappers;

import java.util.List;

import org.mapstruct.Mapper;



import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.entities.Hashtag;

@Mapper(componentModel = "spring", uses = { TweetMapper.class })
public interface HashtagMapper {
	Hashtag dtoToEntity(HashtagDto hashtagDto);
	
	HashtagDto entityToDto(Hashtag hashtag);
	
	List<Hashtag> dtosToEntities(List<HashtagDto> hashtagDtos);
	
	List<HashtagDto> entitiesToDtos(List<Hashtag> hashtag);
}
