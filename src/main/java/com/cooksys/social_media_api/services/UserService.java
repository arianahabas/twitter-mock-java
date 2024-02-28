package com.cooksys.social_media_api.services;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
}
