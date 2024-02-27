package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cooksys.social_media_api.entities.Tweet;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HashtagDto {

    private Long id;

    private String label;

    private Timestamp firstUsed;

    private Timestamp lastUsed;

    private List<TweetResponseDto> tweets;
}

