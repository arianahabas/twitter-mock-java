package com.cooksys.social_media_api.services;


public interface ValidateService {
    boolean validateHashtagExists(String label);
    
    boolean validateUsernameExists(String username);

	boolean validateUsernameAvailable(String username);

}
