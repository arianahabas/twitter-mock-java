package com.cooksys.social_media_api.services.impl;

import com.cooksys.social_media_api.entities.Hashtag;
import com.cooksys.social_media_api.repositories.HashtagRepository;
import com.cooksys.social_media_api.services.HashtagService;
import com.cooksys.social_media_api.services.ValidateService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final HashtagRepository hashtagRepository;

    @Override
    public boolean validateHashtagExists(String label) {
        // Append '#' symbol to the label if it doesn't start with '#'
        if (!label.startsWith("#")) {
            label = "#" + label;
        }

        Optional<Hashtag> hashtagOptional = hashtagRepository.findByLabelIgnoreCase(label);
        return hashtagOptional.isPresent();
    }
}
