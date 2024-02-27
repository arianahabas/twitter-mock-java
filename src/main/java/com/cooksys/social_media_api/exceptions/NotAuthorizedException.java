package com.cooksys.social_media_api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException{

    private static long serialVersionUID = -223444603129608376L;

    private String message;
}
