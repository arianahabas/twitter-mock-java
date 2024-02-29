package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();

    TweetResponseDto getTweetById(Long id);

    List<TweetResponseDto> replyToTweet(CredentialsDto credentialsDto, Long id);
}
