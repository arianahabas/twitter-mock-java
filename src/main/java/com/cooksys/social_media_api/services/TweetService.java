package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();

    TweetResponseDto getTweetById(Long id);

    TweetResponseDto replyToTweet(TweetRequestDto tweetRequestDto, Long id);

    List<TweetResponseDto> getAllTweetReplies(Long id);
}
