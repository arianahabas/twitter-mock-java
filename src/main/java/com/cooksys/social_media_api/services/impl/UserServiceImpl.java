package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.*;
import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.exceptions.BadRequestException;
import com.cooksys.social_media_api.exceptions.NotAuthorizedException;
import com.cooksys.social_media_api.exceptions.NotFoundException;
import com.cooksys.social_media_api.mappers.CredentialsMapper;
import com.cooksys.social_media_api.mappers.ProfileMapper;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TweetMapper tweetMapper;

    private final TweetRepository tweetRepository;

    private final ProfileMapper profileMapper;

    private final CredentialsMapper credentialsMapper;

    /**
     * Checks to see if the user exists
     *
     * @param username is coming from the credentials or the path variable
     * @return A optional User
     */
    public Optional<User> activeUserCheck(String username) {
        Optional<User> user = userRepository.findByCredentialsUsername(username);

        if (user.isEmpty() || user.get().isDeleted()) {
            throw new BadRequestException("User does not exist.");
        }

        return user;
    }

    /**
     * Finds all related tweets to one user.
     *
     * @param tweets List of tweets
     * @return List of non Deleted tweets
     */
    public List<Tweet> findsAllRelatedTweets(List<Tweet> tweets) {
        List<Tweet> allTweets = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (!tweet.isDeleted()) {
                allTweets.add(tweet);
            }
        }
        return allTweets;
    }

    /**
     * Verifying if the credentials are correct and not left empty
     *
     * @param userRequestDto provided by the user
     */
    public void credentialsCheck(UserRequestDto userRequestDto) {
        //Validation check -> Making sure credentials are provided.
        if (userRequestDto.getCredentials() == null) {
            throw new NotAuthorizedException("Credentials are required");
        }
        //Validation check -> Making sure all fields are provided in Credentials.
        if (userRequestDto.getCredentials().getUsername() == null && userRequestDto.getCredentials().getPassword() == null) {
            throw new NotAuthorizedException("Credentials are required");
        } else if (userRequestDto.getCredentials().getUsername() == null) {
            throw new NotAuthorizedException("Username is required");
        } else if (userRequestDto.getCredentials().getPassword() == null) {
            throw new NotAuthorizedException("Password is required");
        }

        Optional<User> userApplyingToTweet = userRepository.findByCredentialsUsername(userRequestDto.getCredentials().getUsername());

        if (userApplyingToTweet.isEmpty()) {
            throw new BadRequestException("User does not exists");
        }

        //Validation check -> password and username
        if (!userRequestDto.getCredentials().getPassword().equals(userApplyingToTweet.get().getCredentials().getPassword())) {
            throw new NotAuthorizedException("Password does not match");
        }
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        //Converts User dto into entity
        User user = userMapper.requestDtoToEntity(userRequestDto);

        Credentials credentials = user.getCredentials();

        Profile profile = user.getProfile();

        //Validation check -> Credentials and Profile together
        if (credentials == null && profile == null) {
            throw new BadRequestException("Credentials and Profile are required");
        }

        //Validation check -> Credentials, Username or password is not empty
        if (credentials == null) {
            throw new BadRequestException("Credentials are required");
        } else if (credentials.getUsername() == null) {
            throw new BadRequestException("Username is required");
        } else if (credentials.getPassword() == null) {
            throw new BadRequestException("Password is required");
        }

        //Validation check -> Profile or Email is not empty
        if (profile == null) {
            throw new BadRequestException("Profile is required");
        } else if (profile.getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        //Validation check -> Duplication of username
        Optional<User> userCheck = userRepository.findByCredentialsUsername(credentials.getUsername());

        //TODO: Need to figure out why it is not creating a second user and a deleted user

        if(userCheck.isPresent()){
            if (userCheck.get().isDeleted()){
                User reviveDeletedUser = userCheck.get();
                reviveDeletedUser.setDeleted(false);
                reviveDeletedUser.getCredentials().setPassword(credentials.getPassword());
                return userMapper.entityToResponseDto(userRepository.saveAndFlush(reviveDeletedUser));
                /*
                System.out.println("Enters into function");
                user.setDeleted(false);
                user.getCredentials().setPassword(credentials.getPassword());
                user.setProfile(profile);

                 */
            }else if (userRepository.existsByCredentialsUsername(userRequestDto.getCredentials().getUsername())) {
                throw new BadRequestException("Username already exists");
            }
        }

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findAllByDeletedFalse());
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isDeleted()) {
                throw new BadRequestException("User with username: " + username + " has been deleted");
            }
            return userMapper.entityToResponseDto(user);
        } else {
            throw new NotFoundException("User with username: " + username + " not found");
        }

    }

    @Override
    public List<UserResponseDto> getFollowers(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);

        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User with username: " + username + " not found");
        }

        User user = optionalUser.get();

        if (user.isDeleted()) {
            throw new BadRequestException("User with username: " + username + " has been deleted");
        }

        List<User> followers = new ArrayList<>();

        for (User u : user.getFollowing()) {
            if (!u.isDeleted()) {
                followers.add(u);
            }
        }

        return userMapper.entitiesToResponseDtos(followers);
    }

    @Override
    public List<TweetResponseDto> getAllUserFeed(String username) {
        //Validation check -> Checking if user exist
        Optional<User> user = activeUserCheck(username);

        List<Tweet> nonDeletedTweets = new ArrayList<>();

        List<Tweet> simpleTweets = user.get().getTweets();

        nonDeletedTweets = findsAllRelatedTweets(simpleTweets);

        //Reversed Chronological Order
        nonDeletedTweets.sort(Comparator.comparing(Tweet::getPosted).reversed());

        List<User> following = user.get().getFollowing();

        /*
        TODO: Ask question if all Tweets
        as well as all (non-deleted) tweets authored by users the given user is following.
        This includes simple tweets, reposts, and replies.
        */

        return tweetMapper.entitiesToResponseDtos(nonDeletedTweets);
    }

    @Override
    public List<TweetResponseDto> getAllUserMentionedTweets(String username) {
        //Validation check -> Checking if user exist
        Optional<User> user = activeUserCheck(username);

        List<Tweet> nonDeletedMentionedTweets = new ArrayList<>();

        List<Tweet> allTweetsInRepository = tweetRepository.findAll();

        //Iterating through all the tweets to see if @username is in the content
        for (Tweet tweet : allTweetsInRepository) {
            System.out.println(tweet.getContent());
            String findUsername = "@" + username;
            if (tweet.getContent() != null && tweet.getContent().contains(findUsername) && !tweet.isDeleted()) {
                System.out.println("Enter in ");
                nonDeletedMentionedTweets.add(tweet);
            }
        }

        //Reverse Chronological Order
        nonDeletedMentionedTweets.sort(Comparator.comparing(Tweet::getPosted).reversed());

        return tweetMapper.entitiesToResponseDtos(nonDeletedMentionedTweets);
    }

    @Override
    public List<UserResponseDto> getAllUsersFollowingProvidedUser(String username) {
        //Validation check -> Checking if user exist
        Optional<User> user = activeUserCheck(username);

        return userMapper.entitiesToResponseDtos(user.get().getFollowers());
    }

    /*
    TODO: Need to figure out how to not add NULL values.
     */
    @Override
    public UserResponseDto updateUserProfile(UserRequestDto userRequestDto, String username) {
        //Validation check -> Checking if user exist
        Optional<User> user = activeUserCheck(username);

        //Validation check -> Credentials are correct
        credentialsCheck(userRequestDto);


        //Validation check -> Making sure profile exists
        if (userRequestDto.getProfile() == null) {
            throw new BadRequestException("Profile is required");
        }

        ProfileDto newProfileDto = userRequestDto.getProfile();

        //If all the fields are left empty then just return the original user's profile
        if (newProfileDto.getFirstName() == null && newProfileDto.getEmail() == null && newProfileDto.getLastName() == null && newProfileDto.getPhone() == null) {
            return userMapper.entityToResponseDto(user.get());
        }

        //Email is required
        if (userRequestDto.getProfile().getEmail() == null) {
            throw new BadRequestException("Email is required");
        }

        user.get().setProfile(profileMapper.dtoToEntity(userRequestDto.getProfile()));

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user.get()));
    }

    @Override
    public void subscribeUser(CredentialsDto credentialsDto, String username) {

        Optional<User> optionalUserToFollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
        if (!optionalUserToFollow.isPresent()) {
            throw new NotFoundException("User " + username + " does not exist or has been deleted.");
        }
        User userToFollow = optionalUserToFollow.get();



        Optional<User> optionalRequester = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentialsDto.getUsername(), credentialsDto.getPassword());
        if (!optionalRequester.isPresent()) {
            throw new NotFoundException("Invalid credentials or user does not exist.");
        }
        User requester = optionalRequester.get();


        if (requester.getFollowers().contains(userToFollow)) {
            throw new NotFoundException("Already following ");
        }

        requester.getFollowers().add(userToFollow);

        userRepository.saveAndFlush(requester);
    }

    public List<TweetResponseDto> getUserTweets(String username) {
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User with username: " + username + " not found");
        }
        User user = optionalUser.get();

        if (user.isDeleted()) {
            throw new BadRequestException("User with username: " + username + " has been deleted");
        }
        return tweetMapper.entitiesToResponseDtos(user.getTweets());

    }

    @Override
    public UserResponseDto deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByCredentialsUsername(username);

        if (!userOptional.isPresent() || userOptional.get().isDeleted()) {
            throw new NotFoundException("User with username: " + username + " is not present");
        }

        User user = userOptional.get();

        user.setDeleted(true); // Mark user as deleted
        userRepository.save(user);

        // Convert and return user data prior to deletion
        return userMapper.entityToResponseDto(user);
    }


    @Override
    public void unsubscribeUser(CredentialsDto credentialsDto, String username) {
        // get the user to unfollow and check credentials and deleted status
        Optional<User> optionalUserToUnfollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
        if (!optionalUserToUnfollow.isPresent()) {
            throw new NotFoundException("User " + username + " does not exist or has been deleted.");
        }
        User userToUnfollow = optionalUserToUnfollow.get();


        // Get the follower and verify the credentials match an active user
        Optional<User> optionalFollower = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentialsDto.getUsername(), credentialsDto.getPassword());
        if (!optionalFollower.isPresent()) {
            throw new NotFoundException("Invalid credentials or user does not exist.");
        }
        User follower = optionalFollower.get();

        // Check if the follower is actually following the user to unfollow
        if (!follower.getFollowers().contains(userToUnfollow)) {
            throw new NotFoundException("No following relationship exists between " + credentialsDto.getUsername() + " and " + username);
        }

        // Remove the following relationship
        follower.getFollowers().remove(userToUnfollow);

        // Save changes
        userRepository.saveAndFlush(follower);

    }
}
