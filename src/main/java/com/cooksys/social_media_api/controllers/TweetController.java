package com.cooksys.social_media_api.controllers;

import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
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

    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweets();
    }

    @GetMapping("{id}")
    public TweetResponseDto getTweetById(@PathVariable Long id) {
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



}
