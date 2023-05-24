package com.dz.app.wordez.dto;

import com.dz.app.wordez.stub.SuggestionsQueryOptions;

public record SuggestionsRequest(String input, SuggestionsQueryOptions suggestionsQueryOptions) {
}
