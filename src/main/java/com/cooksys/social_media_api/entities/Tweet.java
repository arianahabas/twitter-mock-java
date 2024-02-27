package com.cooksys.social_media_api.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Data
public class Tweet {

    @Id
    @GeneratedValue
    private Long id;

    //FK
    private Integer author;

    private Timestamp posted;

    private boolean deleted;

    private String content;

    //FK
    private Integer inReplyTo;

    //FK
    private Integer repostOf;

    /*
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;
     */

}
