package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.HashtagDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.mappers.HashtagMapper;
import com.cooksys.social_media_api.entities.mappers.TweetMapper;
import com.cooksys.social_media_api.repositories.HashtagRepository;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.services.HashtagService;

import com.cooksys.social_media_api.services.exceptions.BadRequestException;
import com.cooksys.social_media_api.services.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    private final HashtagMapper hashtagMapper;

    private final TweetRepository tweetRepository;

    private final TweetMapper tweetMapper;

    /*
    TODO: 2/4 Test are failing to to ask why?

    1. Should contain two values | AssertionError: expected 4 to deeply equal 2

    2. Should contain the first and second hashtag | AssertionError: expected [ '#eldenlord', '#mario', …(2) ] to include 'weAreNumber1'

    */
    @Override
    public List<HashtagDto> getAllHashtags() {
        return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
    }

    /*
    TODO: There are not test to check so I made my own tests. Get clarification tomorrow.
     */
    @Override
    public List<TweetResponseDto> getAllTweetsTaggedWithGivenHashTageLabel(String label) {

        //Finding all the non deleted tweets
        List<Tweet> allNonDeletedTweets = tweetRepository.findAllByDeletedFalse();

        List<Tweet> tweetsTagged = new ArrayList<>();

        //Validation Check -> Label is null
        if(label == null){
            throw new BadRequestException("Need to add a label");
        }

        //Finding all the hashtags with the label provided.
        for(Tweet tweet : allNonDeletedTweets){
            String labelToFind = "#" + label;
            if(tweet.getContent() != null){
               String [] contents = tweet.getContent().split("\s");
               for(String word : contents){
                   if(word.equals(labelToFind)){
                       tweetsTagged.add(tweet);
                   }
               }
            }
        }

        //Validation Check -> No hashtag with the given label exists
        if(tweetsTagged.isEmpty()){
            throw new NotFoundException("#" + label + " does not exist in any tweets");
        }

        //Capturing the timestamps
        List<Timestamp> timestamps = new ArrayList<>();
        for(Tweet tweet : tweetsTagged){
            timestamps.add(tweet.getPosted());
        }

        //Sorting the timestamps
        Collections.sort(timestamps);
        //Reversing the timestamps
        Collections.reverse(timestamps);

        List<Tweet> allTweetsTaggedReversedChronologicalOrder = new ArrayList<>();

        //Timestamp list is in reverse chronological order
        //If the tweet get post is the same time stamp then put it in the above list.
        for(int i = timestamps.size() - 1 ; i >=0 ; i --){
            for(Tweet tweet : tweetsTagged){
                if(tweet.getPosted().equals(timestamps.get(i))){
                    allTweetsTaggedReversedChronologicalOrder.add(tweet);
                }
            }
        }

        return tweetMapper.entitiesToResponseDtos(allTweetsTaggedReversedChronologicalOrder);
    }
}
