package com.cooksys.social_media_api.controllers;

import com.cooksys.social_media_api.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

    public final ValidateService validateService;

    @GetMapping("/tag/exists/{label}")
    public boolean validateHashtagExists(@PathVariable String label){
        return validateService.validateHashtagExists(label);
    }
    
    @GetMapping(" validate/username/exists/@{username}")
    public boolean validateUsernameExists(@PathVariable String username){
    	return validateService.validateUsernameExists(username);
    }
}
