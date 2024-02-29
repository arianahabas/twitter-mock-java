package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.exceptions.NotFoundException;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.UserService;

import com.cooksys.social_media_api.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (credentials == null && profile == null) {
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
        if (profile == null) {
            throw new BadRequestException("Profile is required");
        } else if (profile.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        //Validation check -> Duplication of username
        if (userRepository.existsByCredentialsUsername(credentials.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findAllByDeletedFalse());
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isDeleted()) {
                throw new BadRequestException("User with username: " + username + " has been deleted");
            }
            return userMapper.entityToResponseDto(user);
        } else {
            throw new NotFoundException("User with username: " + username + " not found");
        }
        
    }
        
     @Override
     public List<UserResponseDto> getFollowers(String username){
    	 Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
    	 
    	 if(!optionalUser.isPresent()) {
    		 throw new NotFoundException("User with username: " + username + " not found");
         }
    	 
    	 User user = optionalUser.get();
    	 
    	 if(user.isDeleted()) {
    		 throw new BadRequestException("User with username: " + username + " has been deleted");
    	 }
    	 
    	 List<User> followers = new ArrayList<>();
    	 
    	 for(User u: user.getFollowers()) {
    		 if(!u.isDeleted()) {
    			 followers.add(u);
    		 }
    	 }
    	 
    	 return userMapper.entitiesToResponseDtos(followers);
    }
    
    

}
