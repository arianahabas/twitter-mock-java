package com.cooksys.social_media_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Table(name = "user_table")
@Entity
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @CreationTimestamp
    private Timestamp joined;

    private boolean deleted = false;

    @Embedded
    private Credentials credentials;

    @Embedded
    private Profile profile;

    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets = new ArrayList<>();

    @ManyToMany
    @JoinTable(name ="followers_following")
    private List<User> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    private List<User> following = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id")
    )
    private List<Tweet> likedTweets = new ArrayList<>();

    @ManyToMany(mappedBy = "mentionedUsers")
    private List<Tweet> mentionedTweets = new ArrayList<>();

}
