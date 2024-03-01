package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto findByUsername(String username);

	List<UserResponseDto> getFollowers(String username);

    List<TweetResponseDto> getUserTweets(String username);

	UserResponseDto deleteUser(String username);
}
