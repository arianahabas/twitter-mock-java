package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.mappers.HashtagMapper;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.repositories.HashtagRepository;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.services.HashtagService;

import com.cooksys.social_media_api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    private final HashtagMapper hashtagMapper;

    private final TweetRepository tweetRepository;

    private final TweetMapper tweetMapper;

    @Override
    public List<HashtagDto> getAllHashtags() {
        return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
    }

    @Override
    public List<TweetResponseDto> getAllTweetsTaggedWithGivenHashTagLabel(String label) {

        //Finding all the non deleted tweets
        List<Tweet> allNonDeletedTweets = tweetRepository.findAllByDeletedFalse();

        List<Tweet> tweetsTagged = new ArrayList<>();

        //Find all hashtags
        for(Tweet tweet : allNonDeletedTweets){
            String tagToSearch = "#" + label;
            for(String searchingForLabel : tweet.getContent().split("\s")){
                if(searchingForLabel.equals(tagToSearch)){
                    tweetsTagged.add(tweet);
                }
            }
        }

        //Validation Check -> No hashtag with the given label exists
        if(tweetsTagged.isEmpty()){
            throw new NotFoundException("#" + label + " does not exist in any tweets");
        }

        //Sorting the timestamps
        //Entity and the field
        tweetsTagged.sort(Comparator.comparing(Tweet :: getPosted).reversed());

        return tweetMapper.entitiesToResponseDtos(tweetsTagged);
    }
}
