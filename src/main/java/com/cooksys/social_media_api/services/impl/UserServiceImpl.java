package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.UserService;

import com.cooksys.social_media_api.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        //Converts User dto into entity
        User user = userMapper.requestDtoToEntity(userRequestDto);

        Credentials credentials = user.getCredentials();

        Profile profile = user.getProfile();

        //Validation check -> Credentials and Profile together
        if(credentials == null && profile == null){
            throw new BadRequestException("Credentials and Profile are required");
        }

        //Validation check -> Credentials, Username or password is not empty
        if (credentials == null) {
            throw new BadRequestException("Credentials are required");
        } else if (credentials.getUsername() == null) {
            throw new BadRequestException("Username is required");
        } else if (credentials.getPassword() == null) {
            throw new BadRequestException("Password is required");
        }

        //Validation check -> Profile or Email is not empty
        if(profile == null){
            throw new BadRequestException("Profile is required");
        } else if (profile.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        //Validation check -> Duplication of username
        if(userRepository.existsByCredentialsUsername(credentials.getUsername())){
            throw new BadRequestException("Username already exists");
        }

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));
    }
}
