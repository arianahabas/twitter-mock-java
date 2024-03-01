package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.dtos.ProfileDto;
import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.dtos.UserRequestDto;
import com.cooksys.social_media_api.dtos.UserResponseDto;
import com.cooksys.social_media_api.entities.Credentials;
import com.cooksys.social_media_api.entities.Profile;
import com.cooksys.social_media_api.entities.Tweet;
import com.cooksys.social_media_api.entities.User;
import com.cooksys.social_media_api.exceptions.NotAuthorizedException;
import com.cooksys.social_media_api.exceptions.NotFoundException;
import com.cooksys.social_media_api.mappers.ProfileMapper;
import com.cooksys.social_media_api.exceptions.BadRequestException;
import com.cooksys.social_media_api.mappers.TweetMapper;
import com.cooksys.social_media_api.mappers.UserMapper;
import com.cooksys.social_media_api.repositories.TweetRepository;
import com.cooksys.social_media_api.repositories.UserRepository;
import com.cooksys.social_media_api.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final TweetMapper tweetMapper;

    private final TweetRepository tweetRepository;

    private final ProfileMapper profileMapper;

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
        if(userRequestDto.getCredentials() == null){
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
        if (userRepository.existsByCredentialsUsername(credentials.getUsername())) {
            throw new BadRequestException("Username already exists");
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

        for (User u : user.getFollowers()) {
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
    public void subscribeUser(UserRequestDto userRequestDto, String username) {
        //Validation check -> Checking if user exist
        Optional<User> userToBeFollowed = activeUserCheck(username);

        //Validation check -> Checking credentials
        credentialsCheck(userRequestDto);

        List<User> currentFollowers = userToBeFollowed.get().getFollowing();

        currentFollowers.removeIf(User::isDeleted);

        User userToBeChecked = userMapper.requestDtoToEntity(userRequestDto);

        for (User c : currentFollowers) {
            if (c.getCredentials().getUsername().compareTo(userToBeChecked.getCredentials().getUsername()) < 0) {
                System.out.println("Enter into compare " + userToBeChecked.getCredentials().getUsername());
            } else {
                System.out.println("Doesn'enter " + userToBeChecked.getCredentials().getUsername());
            }
        }


        if (!currentFollowers.contains(userToBeChecked)) {
            System.out.println("Enters in with " + userRequestDto.getCredentials().getUsername());
            currentFollowers.add(userMapper.requestDtoToEntity(userRequestDto));
            userToBeFollowed.get().setFollowing(currentFollowers);
            userRepository.saveAndFlush(userToBeFollowed.get());
        } else {
            throw new BadRequestException("Already following");
        }
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
        Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User with username: " + username + " not found");
        }

        //creating deep copy
        User user = new User();
        user.setCredentials(optionalUser.get().getCredentials());
        user.setFollowers(optionalUser.get().getFollowers());
        user.setFollowing(optionalUser.get().getFollowing());
        user.setId(optionalUser.get().getId());
        user.setJoined(optionalUser.get().getJoined());
        user.setLikedTweets(optionalUser.get().getLikedTweets());
        user.setMentionedTweets(optionalUser.get().getMentionedTweets());
        user.setProfile(optionalUser.get().getProfile());
        user.setTweets(optionalUser.get().getTweets());
        user.setDeleted(false);

        optionalUser.get().setDeleted(true);

        return userMapper.entityToResponseDto(user);
    }
}
