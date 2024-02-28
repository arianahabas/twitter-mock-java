package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.services.TweetService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;

    @Override
    public List<TweetResponseDto> getAllTweets() {
        return tweetMapper.entitiesToResponseDtos(tweetRepository.findAllByDeletedFalseOrderByPostedDesc());
    }
}
