package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import java.util.List;

import com.cooksys.social_media_api.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {

    private String username;

    private Timestamp joined;

    private ProfileDto profile;

}
