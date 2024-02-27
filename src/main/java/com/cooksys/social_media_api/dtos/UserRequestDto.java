package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequestDto {

    private Timestamp joined;

    private boolean deleted =false;

    private CredentialsDto credentials;

    private ProfileDto profile;

    private List<TweetRequestDto> tweets;

    private List<UserRequestDto> followers;

    private List<UserRequestDto> following;

    private List<TweetRequestDto> likedTweets;

    private List<TweetRequestDto> mentionedTweets;

}
