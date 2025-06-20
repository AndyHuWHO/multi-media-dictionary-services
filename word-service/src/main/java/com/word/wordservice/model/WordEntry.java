package com.word.wordservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document("gpt_word_entries")
public class WordEntry {
    @Id
    private String id;
    @Indexed(unique = true)
    private String word;
    private List<DictionaryInfo> dictionaryInfoList;

}
