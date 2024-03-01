package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.TweetRequestDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Hashtag;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.exceptions.BadRequestException;
import com.cooksys.social_media_api.exceptions.NotAuthorizedException;
import com.cooksys.social_media_api.exceptions.NotFoundException;
import com.cooksys.social_media_api.mappers.CredentialsMapper;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.HashtagRepository;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialsMapper credentialsMapper;
    private final HashtagRepository hashtagRepository;
    
    
    @Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
    	Credentials credentials = credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials());
    	
    	if (credentials == null) {
    		throw new BadRequestException("Credentials are required");
    	}
    	
    	if(!userRepository.existsByCredentialsUsername(credentials.getUsername())) {
    		throw new BadRequestException("Invalid Author");
    	}
    	
    	if(credentials.getUsername() == null) {
    		throw new BadRequestException("Username required");
    	}
    	
    	if(credentials.getPassword() == null) {
    		throw new BadRequestException("Password Required");
    	}
    	
    	Tweet tweet = tweetMapper.requestDtoToEntity(tweetRequestDto);
    	
    	if(tweet.getContent() == null) {
    		throw new BadRequestException("Content cannot be empty");
    	}
    	//The above statements ensure that findByCredentialsUsername will always return an object
    	User author = userRepository.findByCredentialsUsername(credentials.getUsername()).get();
    	tweet.setAuthor(author);

    	
    	for(Hashtag h : tweet.getHashtags()) {
    		hashtagRepository.saveAndFlush(h);
    	}
    	
    	tweetRepository.saveAndFlush(tweet);
    	return tweetMapper.entityToResponseDto(tweet);
	}


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

        if (!toReplyTweet.isPresent() || toReplyTweet.get().isDeleted()) {
            throw new NotFoundException("Tweet " + id + " not found");
        }

        //Validation check -> Making sure content is provided.
        if (tweetRequestDto.getContent() == null) {
            throw new BadRequestException("Content is required");
        }

        //Validation check -> Making sure all fields are provided in Credentials.
        if (tweetRequestDto.getCredentials().getUsername() == null && tweetRequestDto.getCredentials().getPassword() == null) {
            throw new NotAuthorizedException("Credentials are required");
        } else if (tweetRequestDto.getCredentials().getUsername() == null) {
            throw new NotAuthorizedException("Username is required");
        } else if (tweetRequestDto.getCredentials().getPassword() == null) {
            throw new NotAuthorizedException("Password is required");
        }

        Optional<User> userApplyingToTweet = userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername());

        /*
        TODO: Create a helper method to verify credentials
         */
        //Validation check -> if username exists
        if (userApplyingToTweet.isEmpty()) {
            throw new BadRequestException("User does not exists");
        }

        //Validation check -> password and username
        if (!tweetRequestDto.getCredentials().getPassword().equals(userApplyingToTweet.get().getCredentials().getPassword())) {
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
        for (String word : tweetRequestDto.getContent().split("\s")) {
            if (word.contains("@")) {
                usernameMentioned.add(word);
            }
            if (word.contains("#")) {
                hashtags.add(word);
            }
        }

        return tweetMapper.entityToResponseDto(tweetRepository.saveAndFlush(newReplyTweet));
    }

    @Override
    public List<TweetResponseDto> getAllTweetReplies(Long id) {
        Optional<Tweet> tweet = tweetRepository.findById(id);
        System.out.println(tweet.isEmpty());
        System.out.println(!tweet.get().isDeleted());
        if (tweet.isEmpty() || tweet.get().isDeleted()) {
            throw new NotFoundException("Tweet does not exists");
        }

        List<Tweet> allTweets = tweet.get().getReplies();
        List<Tweet> replyTweets = new ArrayList<>();

        for (Tweet checkTweet : allTweets) {
            if (!checkTweet.isDeleted()) {
                replyTweets.add(checkTweet);
            }
        }
        return tweetMapper.entitiesToResponseDtos(replyTweets);
    }

    public TweetResponseDto deleteTweet(CredentialsDto credentialsDto, Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
        //check if the tweet with id exists
        if (!optionalTweet.isPresent()) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        Tweet tweet = optionalTweet.get();
        if(tweet.isDeleted()){
            throw new BadRequestException("Tweet with id: " + id + " is deleted");
        }
        User user = tweet.getAuthor();
        Credentials credentialsOnTweet = user.getCredentials();
        CredentialsDto credentialsDtoOnTweet = credentialsMapper.entityToDto(credentialsOnTweet);


        //check if the tweets author matches the given credentials - throw error
        if (!credentialsDto.getUsername().equals(credentialsDtoOnTweet.getUsername())) {
            throw new BadRequestException("Tweet with id: " + id + " does not match with the given credentials");
        }

        //change deleted field in the tweet entity to true
        tweet.setDeleted(true);
        //do not delete from the database - only saved with new flag
        tweetRepository.saveAndFlush(tweet);
        //respond with the tweet data of successfully deleted tweet
        return tweetMapper.entityToResponseDto(tweet);
    }
    
    @Override
    public List<UserResponseDto> getLikedBy(Long id){
    	Optional<Tweet> optionalTweet = tweetRepository.findById(id);
    	if(!optionalTweet.isPresent()) {
    		throw new NotFoundException("Tweet with id " + id + " does not exist");
    	}
    	
    	Tweet tweet = optionalTweet.get();
    	
    	if(tweet.isDeleted()) {
    		throw new BadRequestException("Tweet with id " + id + " has been deleted");
    	}
    	
    	List<User> users = new ArrayList<>();
    	
    	for(User u: tweet.getLikedByUsers()) {
    		if(!u.isDeleted()) {
    			users.add(u);
    		}
    	}
    	return userMapper.entitiesToResponseDtos(users);
    }
    
    @Override
    public List<UserResponseDto> getMentionedUsers(Long id){
    	Optional<Tweet> optionalTweet = tweetRepository.findById(id);
    	if(!optionalTweet.isPresent()) {
    		throw new NotFoundException("Tweet with id " + id + " does not exist");
    	}
    	
    	Tweet tweet = optionalTweet.get();
    	
    	if(tweet.isDeleted()) {
    		throw new BadRequestException("Tweet with id " + id + " has been deleted");
    	}
    	
    	List<User> users = new ArrayList<>();
    	
    	for(User u: tweet.getMentionedUsers()) {
    		if(!u.isDeleted()) {
    			users.add(u);
    		}
    	}
    	return userMapper.entitiesToResponseDtos(users);
    }

    @Override
    public List<TweetResponseDto> getTweetReposts(Long id) {
        // Verify the original tweet exists and is not deleted
        if (!tweetRepository.existsByIdAndDeletedFalse(id)) {
            throw new NotFoundException("Tweet with id " + id + " does not exist or has been deleted");
        }

        // query for non-deleted reposts of the tweet
        List<Tweet> reposts = tweetRepository.findByRepostOfIdAndDeletedFalse(id);

        // Convert the reposts to DTOs
        List<TweetResponseDto> result = tweetMapper.entitiesToResponseDtos(reposts);
        return result;
    }

    @Override
    public void likeTweet(CredentialsDto credentialsDto, Long id) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentialsDto.getUsername(), credentialsDto.getPassword());
        if(!optionalUser.isPresent()){
            throw new NotFoundException("Invalid credentials or user does not exist.");
        }
        User user = optionalUser.get();

        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
        if(!optionalTweet.isPresent()){
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }
        Tweet tweet = optionalTweet.get();
        user.getLikedTweets().add(tweet);
        userRepository.saveAndFlush(user);

    }

    @Override
    public List<TweetResponseDto> getAllTweetsWithContext(Long id) {
        return null;
    }
}
