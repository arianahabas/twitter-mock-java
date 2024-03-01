package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.*;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getAllTweets();

    TweetResponseDto getTweetById(Long id);

    TweetResponseDto replyToTweet(TweetRequestDto tweetRequestDto, Long id);

    List<TweetResponseDto> getAllTweetReplies(Long id);
    TweetResponseDto deleteTweet(CredentialsDto credentialsDto, Long id);

	TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

	List<UserResponseDto> getLikedBy(Long id);

    void likeTweet(CredentialsDto credentialsDto, Long id);

	List<UserResponseDto> getMentionedUsers(Long id);


    ContextDto getTweetContext(Long id);


    List<TweetResponseDto> getTweetReposts(Long id);
}
