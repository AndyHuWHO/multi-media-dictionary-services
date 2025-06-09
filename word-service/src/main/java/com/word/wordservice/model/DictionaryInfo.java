package com.word.wordservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DictionaryInfo {
    private String partOfSpeech;
    private Pronunciation pronunciation;
    private List<WordSense> wordSenseList;
}
