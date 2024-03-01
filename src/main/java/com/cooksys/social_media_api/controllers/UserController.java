package com.cooksys.social_media_api.controllers;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
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

    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getAllUserFeed(@PathVariable("username") String username){
        return userService.getAllUserFeed(username);
    }

    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getAllUserMentionedTweets(@PathVariable("username") String username){
        return userService.getAllUserMentionedTweets(username);
    }

    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getAllUsersFollowingProvidedUser(@PathVariable("username") String username){
        return userService.getAllUsersFollowingProvidedUser(username);
    }

    @PatchMapping("/@{username}")
    public UserResponseDto updateUserProfile(@RequestBody UserRequestDto userRequestDto, @PathVariable("username") String username){
        return userService.updateUserProfile(userRequestDto, username);
    }

    @PostMapping("/@{username}/follow")
    public void subscribesUser(@RequestBody CredentialsDto credentialsDto, @PathVariable("username") String username) {
        userService.subscribeUser(credentialsDto, username);
    }

    @PostMapping("/@{username}/unfollow")
    public void unsubscribesUser(@RequestBody CredentialsDto credentialsDto, @PathVariable("username") String username) {
        userService.unsubscribeUser(credentialsDto, username);
    }

    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getFollowers(@PathVariable("username") String username){
    	return userService.getFollowers(username);
    }

    @GetMapping("@{username}/tweets")
    public List<TweetResponseDto> getUserTweets(@PathVariable("username") String username){
        return userService.getUserTweets(username);
    }
    
    @DeleteMapping("@{username}")
    public UserResponseDto deleteUser(@PathVariable("username") String username) {
    	return userService.deleteUser(username);
    }
}
