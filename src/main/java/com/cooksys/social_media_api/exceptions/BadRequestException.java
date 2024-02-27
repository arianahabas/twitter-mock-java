package com.cooksys.social_media_api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException {

    private static long serialVersionUID = -4672718297774462786L;

    private String message;
}
