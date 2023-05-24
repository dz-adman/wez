package com.dz.app.wordez.service;

import com.dz.app.wordez.dto.SuggestionsRequest;
import com.dz.app.wordez.dto.WResponse;
import com.dz.app.wordez.dto.WordsRequest;
import org.springframework.stereotype.Service;

@Service
public interface QueryService {
    WResponse wordsResults(long userId, WordsRequest... wordsRequests);
    WResponse suggestionsResults(long userId, SuggestionsRequest... suggestionsRequests);
}
