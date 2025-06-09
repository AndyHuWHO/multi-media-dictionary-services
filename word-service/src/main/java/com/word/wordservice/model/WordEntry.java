package com.word.wordservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("word_entries")
public class WordEntry {
    @Id
    private String id;
    @Indexed
    private String word;
    private List<DictionaryInfo> dictionaryInfoList;
    private List<MediaInfo> mediaInfoList;

    public static WordEntry createDummy(String word) {
        WordSense sense = new WordSense();
        sense.setDefinitionEn("This is a placeholder definition for the word '" + word + "'.");
        sense.setTranslationZh("这是 '" + word + "' 的占位翻译。");
        sense.setSampleExpressions(List.of("example expression 1", "example expression 2"));
        sense.setSampleSentences(List.of(
                "This is a sample sentence using the word '" + word + "'.",
                "Here is another example sentence."
        ));

        DictionaryInfo dictionaryInfo = new DictionaryInfo();
        dictionaryInfo.setPartOfSpeech("noun");
        dictionaryInfo.setPronunciation(new Pronunciation("ˈ" + word + "_uk", "ˈ" + word + "_us"));
        dictionaryInfo.setWordSenseList(List.of(sense));

        WordEntry entry = new WordEntry();
        entry.setWord(word);
        entry.setDictionaryInfoList(List.of(dictionaryInfo));
        entry.setMediaInfoList(List.of());

        return entry;
    }
}
