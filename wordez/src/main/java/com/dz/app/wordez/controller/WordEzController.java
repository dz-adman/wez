package com.dz.app.wordez.controller;

import com.dz.app.wordez.dto.Result;
import com.dz.app.wordez.dto.SuggestionsRequest;
import com.dz.app.wordez.dto.WResponse;
import com.dz.app.wordez.dto.WordsRequest;
import com.dz.app.wordez.service.QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@Tag(name = "WEZ", description = "WordEZ APIs")
@RequestMapping("/wez")
@RequiredArgsConstructor
public class WordEzController {

    private final QueryService queryService;

    @Operation(
            summary = "Words Query",
            description = "Shoot your words related queries",
            tags = "WEZ"
    )
    @PostMapping(path = "/words", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Result> wordsQueryResults(@RequestHeader("x-user-id") Long userId, @RequestBody WordsRequest... wordsRequest) {
        if (ObjectUtils.isEmpty(userId)) throw new IllegalArgumentException("UserId not found!");
        WResponse wResponse = queryService.wordsResults(userId, wordsRequest);
        return Flux.fromIterable(wResponse.getResults())
                .delayElements(Duration.of(250, ChronoUnit.MILLIS));
    }

    @Operation(
            summary = "Suggestions Query",
            description = "Shoot your suggestions related queries",
            tags = "WEZ"
    )
    @PostMapping(path = "/suggestions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Result> suggestionsQueryResult(@RequestHeader("x-user-id") Long userId, @RequestBody SuggestionsRequest... suggestionsRequest) {
        if (ObjectUtils.isEmpty(userId)) throw new IllegalArgumentException("UserId not found!");
        WResponse wResponse = queryService.suggestionsResults(0, suggestionsRequest);
        return Flux.fromIterable(wResponse.getResults())
                .delayElements(Duration.of(250, ChronoUnit.MILLIS));
    }
}
