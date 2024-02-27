package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.List;

import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TweetRequestDto {

    private UserRequestDto author;

    private Timestamp posted;

    private boolean deleted = false;

    private String content;

    private TweetRequestDto inReplyTo;

    private TweetRequestDto repostOf;

    private List<TweetRequestDto> replies;

    private List<TweetRequestDto> reposts;
 
    private List<UserRequestDto> mentionedUsers ;

    private List<HashtagDto> hashtags;

    private List<UserRequestDto> likedByUsers;

}
