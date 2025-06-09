package com.word.wordservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
public class WordSense {
    private String definitionEn;
    private String translationZh;
    private List<String> sampleExpressions;
    private List<String> sampleSentences;
}
