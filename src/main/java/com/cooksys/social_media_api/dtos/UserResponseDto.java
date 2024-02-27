package com.cooksys.social_media_api.dtos;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDto {

    private String username;

    private Timestamp joined;

    private ProfileDto profile;

}
