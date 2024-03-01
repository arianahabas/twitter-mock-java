package com.cooksys.social_media_api.repositories;

import com.cooksys.social_media_api.dtos.TweetResponseDto;
import com.cooksys.social_media_api.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findAllByDeletedFalseOrderByPostedDesc();

    //Finds all non deleted tweets
    List<Tweet> findAllByDeletedFalse();

    Optional<Tweet> findById(Long id);

    boolean existsByIdAndDeletedFalse(Long id);

    Optional<Tweet> findByIdAndDeletedFalse(Long id);

    List<Tweet> findByRepostOfIdAndDeletedFalse(Long id);


}