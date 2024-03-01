package com.cooksys.social_media_api.controllers;


import com.cooksys.social_media_api.dtos.*;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;

import com.cooksys.social_media_api.services.TweetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;
    
    @PostMapping
    public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto){
    	return tweetService.createTweet(tweetRequestDto);
    }

    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweets();
    }

    @GetMapping("{id}")
    public TweetResponseDto getTweetById(@PathVariable("id")  Long id) {
        return tweetService.getTweetById(id);
    }

    @PostMapping("/{id}/reply")
    public TweetResponseDto replyToTweet(@RequestBody TweetRequestDto tweetRequestDto, @PathVariable("id") Long id){
        return tweetService.replyToTweet(tweetRequestDto, id);
    }

    @GetMapping("{id}/replies")
    public List<TweetResponseDto> getAllTweetReplies(@PathVariable("id") Long id){
        return tweetService.getAllTweetReplies(id);
    }

    @DeleteMapping("{id}")
    public TweetResponseDto deleteTweet(@RequestBody CredentialsDto credentialsDto, @PathVariable("id") Long id){
        return tweetService.deleteTweet(credentialsDto, id);
    }
    
    @GetMapping("/{id}/likes")
    public List<UserResponseDto> getLikedBy(@PathVariable("id") Long id){
    	return tweetService.getLikedBy(id);
    }
    
    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentionedUsers(@PathVariable("id")  Long id){
    	return tweetService.getMentionedUsers(id);
    }

    @PostMapping("/{id}/like")
    public void likeTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto){
       tweetService.likeTweet(credentialsDto, id);
    }

    @GetMapping("/{id}/context")
    public ContextDto getTweetContext(@PathVariable("id") Long id){
        return tweetService.getTweetContext(id);
    }

    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getTweetReposts(@PathVariable("id") Long id){
        return tweetService.getTweetReposts(id);
    }
    
    @PostMapping("/{id}/repost")
    public TweetResponseDto createRepost(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto) {
    	return tweetService.createRepost(credentialsDto, id);
    }
    
    @GetMapping("/{id}/tags")
    public List<HashtagDto> getTags(@PathVariable("id") Long id) {
    	return tweetService.getHashtags(id);
    }
}
