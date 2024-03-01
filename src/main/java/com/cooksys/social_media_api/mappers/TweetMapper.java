package com.cooksys.social_media_api.mappers;

import org.mapstruct.Mapper;
import java.util.List;
import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;


@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {
	Tweet requestDtoToEntity(TweetRequestDto tweetRequestDto);
	
	Tweet responseDtoToEntity(TweetResponseDto tweetResponseDto);
	
	TweetRequestDto entityToRequestDto(Tweet tweet);
	
	TweetResponseDto entityToResponseDto(Tweet tweet);

	List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets);

}
