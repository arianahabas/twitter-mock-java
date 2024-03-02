package com.cooksys.social_media_api.services.impl;


import com.cooksys.social_media_api.dtos.*;

import com.cooksys.social_media_api.dtos.CredentialsDto;
import com.cooksys.social_media_api.dtos.HashtagDto;
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
import com.cooksys.social_media_api.mappers.HashtagMapper;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.HashtagRepository;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.annotation.Target;
import java.sql.Timestamp;
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
    private final HashtagMapper hashtagMapper;

    public Tweet validateAndGetTweetById(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findById(id);
        if (optionalTweet.isEmpty()) {
            throw new NotFoundException("Tweet with id " + id + " does not exist");
        }

        Tweet tweet = optionalTweet.get();

        if (tweet.isDeleted()) {
            throw new BadRequestException("Tweet with id " + id + " has been deleted");
        }
        return tweet;
    }

    public User validateAndGetUserByCredentials(CredentialsDto credentialsDto) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(
                credentialsDto.getUsername(), credentialsDto.getPassword());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Invalid credentials or user does not exist.");
        }
        return optionalUser.get();
    }


    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
        Credentials credentials = credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials());

        if (credentials == null) {
            throw new BadRequestException("Credentials are required");
        }

        if (!userRepository.existsByCredentialsUsername(credentials.getUsername())) {
            throw new BadRequestException("Invalid Author");
        }

        if (credentials.getUsername() == null) {
            throw new BadRequestException("Username required");
        }

        if (credentials.getPassword() == null) {
            throw new BadRequestException("Password Required");
        }

        Tweet tweet = tweetMapper.requestDtoToEntity(tweetRequestDto);

        if (tweet.getContent() == null) {
            throw new BadRequestException("Content cannot be empty");
        }
        //The above statements ensure that findByCredentialsUsername will always return an object
        User author = userRepository.findByCredentialsUsername(credentials.getUsername()).get();
        tweet.setAuthor(author);


        String content[] = tweet.getContent().split("\\s+");

        for (int x = 0; x < content.length; x++) {
            if (content[x].charAt(0) == '#') {
                if (hashtagRepository.findByLabelIgnoreCase(content[x].substring(1)).isPresent()) {
                    hashtagRepository.findByLabelIgnoreCase(content[x].substring(1)).get().getTweets().add(tweet);
                } else {
                    Hashtag hashtag = new Hashtag();
                    hashtag.setLabel(content[x].substring(1));
                    hashtag.getTweets().add(tweet);
                    tweet.getHashtags().add(hashtag);
                    hashtagRepository.saveAndFlush(hashtag);
                }
            }

            if (content[x].charAt(0) == '@') {
                User user = userRepository.findByCredentialsUsername(content[x].substring(1)).get();
                tweet.getMentionedUsers().add(user);

            }
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
        return tweetMapper.entityToResponseDto(validateAndGetTweetById(id));
    }

    public TweetResponseDto replyToTweet(TweetRequestDto tweetRequestDto, Long id) {
        Tweet toReplyTweet = validateAndGetTweetById(id);

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
        newReplyTweet.setInReplyTo(toReplyTweet);
        newReplyTweet.setAuthor(userApplyingToTweet.get());
        newReplyTweet.setContent(tweetRequestDto.getContent());

        /*
        TODO: Create a helper method to get @Username or #Hashtag
        Takes a content and checks if there are mentions or hashtags
        Need more clarification on this one
         */
        List<String> usernameMentioned = new ArrayList<>();
        List<String> hashtags = new ArrayList<>();
        for (String word : tweetRequestDto.getContent().split(" ")) {
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
        Tweet tweet = validateAndGetTweetById(id);
        List<Tweet> allTweets = tweet.getReplies();
        List<Tweet> replyTweets = new ArrayList<>();

        for (Tweet checkTweet : allTweets) {
            if (!checkTweet.isDeleted()) {
                replyTweets.add(checkTweet);
            }
        }
        return tweetMapper.entitiesToResponseDtos(replyTweets);
    }

    public TweetResponseDto deleteTweet(CredentialsDto credentialsDto, Long id) {
        Tweet tweet = validateAndGetTweetById(id);
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
    public List<UserResponseDto> getLikedBy(Long id) {
        Tweet tweet = validateAndGetTweetById(id);

        List<User> users = new ArrayList<>();

        for (User u : tweet.getLikedByUsers()) {
            if (!u.isDeleted() && !users.contains(u)) {
                users.add(u);
            }
        }
        return userMapper.entitiesToResponseDtos(users);
    }

    @Override
    public List<UserResponseDto> getMentionedUsers(Long id) {
        Tweet tweet = validateAndGetTweetById(id);

        List<User> users = new ArrayList<>();

        for (User u : tweet.getMentionedUsers()) {
            if (!u.isDeleted()) {
                users.add(u);
            }
        }
        return userMapper.entitiesToResponseDtos(users);
    }

    @Override
    public List<TweetResponseDto> getTweetReposts(Long id) {
        List<Tweet> reposts = tweetRepository.findByRepostOfIdAndDeletedFalse(id);

        return tweetMapper.entitiesToResponseDtos(reposts);
    }


    @Override
    public void likeTweet(CredentialsDto credentialsDto, Long id) {
        User user = validateAndGetUserByCredentials(credentialsDto);
        Tweet tweet = validateAndGetTweetById(id);
        user.getLikedTweets().add(tweet);
        userRepository.saveAndFlush(user);

    }

    /**
     * Gathers replies to a given tweet, categorizing them as before or after the target tweet's posting time.
     *
     * @param tweet      The target tweet for which replies are being gathered.
     * @param before     A list of replies posted before the target tweet.
     * @param after      A list of replies posted after the target tweet.
     */
    private void gatherReplies(Tweet tweet, List<TweetResponseDto> before, List<TweetResponseDto> after) {

        //Need to first check if the original tweet has in reply to.
        if(!tweet.isDeleted()){

            TweetResponseDto initialReplyDto = tweetMapper.entityToResponseDto(tweet);

            if(initialReplyDto != null){

                before.add(initialReplyDto.getInReplyTo());
            }

        }

        // Iterate over all replies to the current tweet
        for (Tweet reply : tweet.getReplies()) {
            // Skip any replies that are marked as deleted
            if (!reply.isDeleted()) {
                // Convert the reply to a DTO for inclusion in the response
                TweetResponseDto replyDto = tweetMapper.entityToResponseDto(reply);

                if(replyDto != null){
                    //Adding the in replies to
                    before.add(replyDto.getInReplyTo());
                    //Adding the replies
                    after.add(replyDto);
                }

            }
            // Gather replies to this reply, using the same target time
            gatherReplies(reply, before, after);
        }
    }

    @Override
    public ContextDto getTweetContext(Long id) {
        Tweet targetTweet = validateAndGetTweetById(id);

        // Initialize the context DTO and set the target tweet
        ContextDto context = new ContextDto();
        context.setTarget(tweetMapper.entityToResponseDto(targetTweet));

        // Lists to hold replies before and after the target tweet
        List<TweetResponseDto> before = new ArrayList<>();
        List<TweetResponseDto> after = new ArrayList<>();

        // Gather all relevant replies, categorizing them as before or after the target tweet
        gatherReplies(targetTweet, before, after);

        //If first element is null that means the tweet reply stop there
        if(before.get(0) == null){
            before.clear();
        }

        // Set the before and after lists in the context DTO
        context.setBefore(before);
        context.setAfter(after);

        // Return the fully populated context DTO
        return context;

    }

    @Override
    public TweetResponseDto createRepost(CredentialsDto credentialsDto, Long id) {
        Tweet originalTweet = validateAndGetTweetById(id);
        User user = validateAndGetUserByCredentials(credentialsDto);

        // Create a new repost tweet
        Tweet repostTweet = new Tweet();
        repostTweet.setContent(""); // Reposts should not have content
        repostTweet.setAuthor(user);
        repostTweet.setRepostOf(originalTweet);

        // Save the repost tweet
        Tweet savedRepostTweet = tweetRepository.saveAndFlush(repostTweet);

        // Return the newly created repost tweet in the response
        return tweetMapper.entityToResponseDto(savedRepostTweet);
    }

    @Override
    public List<HashtagDto> getHashtags(Long id) {
        Tweet tweet = validateAndGetTweetById(id);
        return hashtagMapper.entitiesToDtos(tweet.getHashtags());
    }
}
