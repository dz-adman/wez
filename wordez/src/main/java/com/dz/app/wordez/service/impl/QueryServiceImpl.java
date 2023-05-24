package com.dz.app.wordez.service.impl;

import com.dz.app.wordez.constant.AppConstant;
import com.dz.app.wordez.dao.entity.UserQueryRecord;
import com.dz.app.wordez.dto.Result;
import com.dz.app.wordez.dto.SuggestionsRequest;
import com.dz.app.wordez.dto.WResponse;
import com.dz.app.wordez.dto.WordsRequest;
import com.dz.app.wordez.event.RecordUserQueryEvent;
import com.dz.app.wordez.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final RestTemplate rt;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WResponse wordsResults(long userId, WordsRequest... wordsRequests) {
        String url = "/words?";
        if (wordsRequests.length == 0) throw new IllegalArgumentException("Invalid Request");
        url += wordsUrlBuilder(wordsRequests[0]) + "=" + String.join("+", wordsRequests[0].input().split("\\s"));
        for (int i = 1; i < wordsRequests.length; ++i)
            url += "&" + wordsUrlBuilder(wordsRequests[i]) + "=" + String.join("+", wordsRequests[i].input().split("\\s"));

        return getwResponse(userId, url);
    }

    @Override
    public WResponse suggestionsResults(long userId, SuggestionsRequest... suggestionsRequests) {
        String url = "/sug?";
        if (suggestionsRequests.length == 0) throw new IllegalArgumentException("Invalid Request");
        url += suggestionsUrlBuilder(suggestionsRequests[0]) + "=" + String.join("+", suggestionsRequests[0].input().split("\\s"));
        for (int i = 1; i < suggestionsRequests.length; ++i)
            url += "&" + suggestionsUrlBuilder(suggestionsRequests[i]) + "=" + String.join("+", suggestionsRequests[i].input().split("\\s"));

        return getwResponse(userId, url);
    }

    private WResponse getwResponse(long userId, String url) {
        Instant now = Instant.now();
        ResponseEntity<Result[]> response = rt.getForEntity(AppConstant.BASE_URL + url, Result[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Result[] results = response.getBody();
            publishRecordUserQueryEvent(url, true, com.dz.app.wordez.dao.stub.Result.SUCCESS,
                    Duration.between(now, Instant.now()).toMillis(), now, userId);
            return new WResponse(Arrays.asList(results));
        }
        else {
            publishRecordUserQueryEvent(url, response.getStatusCode().is5xxServerError(), com.dz.app.wordez.dao.stub.Result.FAILURE,
                    Duration.between(now, Instant.now()).toMillis(), now, userId);
            throw new IllegalArgumentException("API Call Failed! ResponseCode : " + response.getStatusCode());
        }
    }

    private String wordsUrlBuilder(WordsRequest wordsRequest) {
        return switch (wordsRequest.wordsQueryOption()) {
            case MEANS_LIKE -> AppConstant.WORDS_ML;
            case SOUNDS_LIKE -> AppConstant.WORDS_SL;
            case SPELLED_LIKE -> AppConstant.WORDS_SP;
            case RELATED_WORD -> "rel_" + switch (wordsRequest.wordsRelatedWordCode()) {
                case POPULAR_NOUNS -> AppConstant.WORDS_REL_CODE_JJA;
                case POPULAR_ADJECTIVES -> AppConstant.WORDS_REL_CODE_JJB;
                case SYNONYMS -> AppConstant.WORDS_REL_CODE_SYN;
                case TRIGGERS -> AppConstant.WORDS_REL_CODE_TRG;
                case ANTONYMS -> AppConstant.WORDS_REL_CODE_ANT;
                case KIND_OF -> AppConstant.WORDS_REL_CODE_SPC;
                case MORE_GENERAL_THAN -> AppConstant.WORDS_REL_CODE_GEN;
                case COMPRISES -> AppConstant.WORDS_REL_CODE_COM;
                case PART_OF -> AppConstant.WORDS_REL_CODE_PAR;
                case FREQUENT_SUCCESSORS -> AppConstant.WORDS_REL_CODE_BGA;
                case FREQUENT_PREDECESSORS -> AppConstant.WORDS_REL_CODE_BGB;
                case RHYMES -> AppConstant.WORDS_REL_CODE_RHY;
                case APPROXIMATE_RHYMES -> AppConstant.WORDS_REL_CODE_NRY;
                case HOMOPHONES_SOUNDS_ALIKE_WORDS -> AppConstant.WORDS_REL_CODE_HOM;
                case CONSONANT_MATCH -> AppConstant.WORDS_REL_CODE_CNS;
            };
            case VOCABULARY_IDENTIFIER -> AppConstant.WORDS_V;
            case TOPICS -> AppConstant.WORDS_TOPICS;
            case LEFT_CONTEXT -> AppConstant.WORDS_LC;
            case RIGHT_CONTEXT -> AppConstant.WORDS_RC;
            case MAX_RESULT_SIZE -> AppConstant.WORDS_MAX;
            case METADATA -> switch (wordsRequest.wordsMetaDataFlag()) {
                case DEFINITIONS -> AppConstant.WORDS_MD_D;
                case PARTS_OF_SPEECH -> AppConstant.WORDS_MD_P;
                case SYLLABLE_COUNT -> AppConstant.WORDS_MD_S;
                case PRONUNCIATION -> AppConstant.WORDS_MD_R;
                case WORD_FREQUENCY -> AppConstant.WORDS_MD_F;
            };
            case QUERY_ECHO -> AppConstant.WORDS_QE;
        };
    }

    private String suggestionsUrlBuilder(SuggestionsRequest suggestionsRequest) {
        return switch (suggestionsRequest.suggestionsQueryOptions()) {
            case PREFIX_HINT_STRING -> AppConstant.SUG_S;
            case MAX_RESULT_SIZE -> AppConstant.SUG_MAX;
            case VOCABULARY_IDENTIFIER -> AppConstant.SUG_V;
        };
    }

    private void publishRecordUserQueryEvent(String url, boolean valid, com.dz.app.wordez.dao.stub.Result result,
                                             long timeTakenInMs, Instant instant, long userId) {
        UserQueryRecord userQueryRecord = UserQueryRecord.builder()
                .url(url)
                .valid(valid)
                .result(result)
                .timeTakenInMs(timeTakenInMs)
                .instant(instant.toString())
                .userId(userId)
                .build();
        eventPublisher.publishEvent(new RecordUserQueryEvent(this, userQueryRecord));
    }
}
