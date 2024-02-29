package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.CredentialsDto;
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

import java.util.List;
import java.util.Optional;

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

    public List<TweetResponseDto> replyToTweet(CredentialsDto credentialsDto, Long id) {

        //Tweet that is being replied
        Optional<Tweet> tweetReply = tweetRepository.findById(id);

        if(tweetReply.isEmpty() || tweetReply.get().isDeleted()){
            throw new NotFoundException("Tweet " + id + " not found");
        }

        //User applying to the tweet
        Optional<User> userApplyingToTweet = userRepository.findByCredentialUsername(credentialsDto.getUsername());

        //Validation check -> password
        if(userApplyingToTweet.isEmpty() || !credentialsDto.getPassword().equals(userApplyingToTweet.get().getCredentials().getPassword())){
            throw new NotAuthorizedException("Credentials do not match");
        }
        return null;
    }
}
