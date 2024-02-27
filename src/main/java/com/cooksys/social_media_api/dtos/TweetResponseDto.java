package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.cooksys.social_media_api.entities.Hashtag;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TweetResponseDto {
	
    private Long id;

    private UserRequestDto author;

    private Timestamp posted;

    private boolean deleted = false;

    private String content;

    private TweetResponseDto inReplyTo;

    private TweetResponseDto repostOf;

    private List<TweetResponseDto> replies;

    private List<TweetResponseDto> reposts;
 
    private List<UserResponseDto> mentionedUsers ;

    private List<HashtagDto> hashtags;

    private List<UserResponseDto> likedByUsers;


}
