package com.cooksys.social_media_api.controllers;

import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.services.UserService;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/@{username}")
    public UserResponseDto getUser(@PathVariable("username") String username) {
        return userService.findByUsername(username);
    }
    
    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getFollowers(@PathVariable("username") String username){
    	return userService.getFollowers(username);
    }
}
