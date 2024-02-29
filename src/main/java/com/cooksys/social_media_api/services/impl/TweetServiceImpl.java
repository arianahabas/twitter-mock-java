package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.exceptions.BadRequestException;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.exceptions.NotAuthorizedException;
import com.cooksys.social_media_api.exceptions.NotFoundException;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.TweetService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserRepository userRepository;

    @Override
    public List<TweetResponseDto> getAllTweets() {
        return tweetMapper.entitiesToResponseDtos(tweetRepository.findAllByDeletedFalseOrderByPostedDesc());
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
        if (optionalTweet.isPresent()) {
            Tweet tweet = optionalTweet.get();
            if (tweet.isDeleted()) {
                throw new BadRequestException("Tweet with id " + id + " has been deleted");
            }
            return tweetMapper.entityToResponseDto(tweet);
        } else {
            throw new NotFoundException("Tweet with id " + id + " not found");
        }
    }

    public TweetResponseDto replyToTweet(TweetRequestDto tweetRequestDto, Long id) {

        //Tweet that is being replied to
        Optional<Tweet> toReplyTweet = tweetRepository.findById(id);

        if(!toReplyTweet.isPresent() || toReplyTweet.get().isDeleted()){
            throw new NotFoundException("Tweet " + id + " not found");
        }

        //Validation check -> Making sure content is provided.
        if(tweetRequestDto.getContent() == null){
            throw new BadRequestException("Content is required");
        }

        //Validation check -> Making sure all fields are provided in Credentials.
        if(tweetRequestDto.getCredentials().getUsername() == null && tweetRequestDto.getCredentials().getPassword() == null){
            throw new NotAuthorizedException("Credentials are required");
        } else if (tweetRequestDto.getCredentials().getUsername() == null) {
            throw new NotAuthorizedException("Username is required");
        } else if(tweetRequestDto.getCredentials().getPassword() == null){
            throw new NotAuthorizedException("Password is required");
        }

        Optional<User> userApplyingToTweet = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername());

        /*
        TODO: Create a helper method to verify credentials
         */
        //Validation check -> if username exists
        if(userApplyingToTweet.isEmpty()){
            throw new BadRequestException("User does not exists");
        }

        System.out.println("pass 1 " + tweetRequestDto.getCredentials().getPassword());
        System.out.println("pass 2 " + userApplyingToTweet.get().getCredentials().getPassword());

        //Validation check -> password and username
        if(!tweetRequestDto.getCredentials().getPassword().equals(userApplyingToTweet.get().getCredentials().getPassword())){
            throw new NotAuthorizedException("Password does not match");
        }

        //Reply to the tweet
        Tweet newReplyTweet = new Tweet();
        newReplyTweet.setInReplyTo(toReplyTweet.get());
        newReplyTweet.setAuthor(userApplyingToTweet.get());
        newReplyTweet.setContent(tweetRequestDto.getContent());


        /*
        TODO: Create a helper method to get @Username or #Hashtag
        Takes a content and checks if there are mentions or hashtags
        Need more clarification on this one
         */
        List<String> usernameMentioned = new ArrayList<>();
        List<String> hashtags = new ArrayList<>();
        for(String word : tweetRequestDto.getContent().split("\s")){
           if(word.contains("@")){
              usernameMentioned.add(word);
           }
           if(word.contains("#")){
               hashtags.add(word);
           }
        }

        return tweetMapper.entityToResponseDto(tweetRepository.saveAndFlush(newReplyTweet));
    }

}
