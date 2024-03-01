package com.cooksys.social_media_api.services;

import org.springframework.web.bind.annotation.PathVariable;

public interface ValidateService {
    boolean validateHashtagExists(String label);
    
    boolean validateUsernameExists(String username);

}
