package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;

import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {
	
    private Long id;

    private Timestamp joined;

    private boolean deleted =false;

    private CredentialsDto credentials;

    private ProfileDto profile;

    private List<TweetResponseDto> tweets;

    private List<UserResponseDto> followers;

    private List<UserResponseDto> following;

    private List<TweetResponseDto> likedTweets;

    private List<TweetResponseDto> mentionedTweets;

}
