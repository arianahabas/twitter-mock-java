package com.cooksys.social_media_api.controllers;

import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.services.HashtagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping
    public List<HashtagDto> getAllHashtags(){
        return hashtagService.getAllHashtags();
    }

    @GetMapping("/{label}")
    public List<TweetResponseDto> getAllTweetsTaggedWithGivenHashtagLabel(@PathVariable("label") String label){
        return hashtagService.getAllTweetsTaggedWithGivenHashTagLabel(label);
    }
}
