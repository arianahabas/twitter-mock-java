package com.cooksys.social_media_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Credentials {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

}
