package com.cooksys.social_media_api.repositories;

import com.cooksys.social_media_api.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    //Finds all non deleted tweets
    List<Tweet> findAllByDeletedFalse();
}
