package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {
    List<HashtagDto> getAllHashtags();

    List<TweetResponseDto> getAllTweetsTaggedWithGivenHashTagLabel(String label);
}
