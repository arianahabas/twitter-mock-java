package com.cooksys.social_media_api.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContextDto {

	TweetResponseDto target;
	
	List<TweetResponseDto> before;
	
	List<TweetResponseDto> After;

}
