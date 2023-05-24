package com.dz.app.wordez.dto;

import com.dz.app.wordez.stub.WordsMetaDataFlag;
import com.dz.app.wordez.stub.WordsRelatedWordCode;
import com.dz.app.wordez.stub.WordsQueryOption;

public record WordsRequest(String input, WordsQueryOption wordsQueryOption, WordsRelatedWordCode wordsRelatedWordCode, WordsMetaDataFlag wordsMetaDataFlag) {
}
