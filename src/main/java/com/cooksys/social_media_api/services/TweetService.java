package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.TweetResponseDto;

import java.util.List;

public interface TweetService {
    List<TweetResponseDto> getAllTweets();
}
