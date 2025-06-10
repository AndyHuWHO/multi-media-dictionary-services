package com.word.wordservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.wordservice.model.WordEntry;
import com.word.wordservice.response.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiDictionaryClient {
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public OpenAiDictionaryClient(WebClient openAiWebClient,
                                  ObjectMapper objectMapper,
                                  @Value("${openai.api.key}") String apiKey) {
        this.openAiWebClient = openAiWebClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }


    public Mono<WordEntry> fetchDictionaryInfo(String word) {
        return openAiWebClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of(
                        "model", "gpt-4.1",
                        "messages", List.of(
                                Map.of("role", "system", "content", SYSTEM_PROMPT),
                                Map.of("role", "user", "content", buildUserPrompt(word))
                        )
                ))
                .retrieve()
                .bodyToMono(OpenAIResponse.class)
                .map(OpenAIResponse::getFirstMessageContent)
                .map(content -> {
                    try {
                        return objectMapper.readValue(content, WordEntry.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse GPT response into WordEntry", e);
                    }
                });
    }
    private String buildUserPrompt(String word) {
        return String.format("""
                        Provide the json object for dictionary information of the word '%s'.
                        The format must match exactly:  
                        {"word": "...",  
                        "dictionaryInfoList": [ 
                            {"partOfSpeech": "...", 
                            "pronunciation": { "uk": "...", "us": "..."},  
                            "wordSenseList": [ 
                                {"definitionEn": "...", 
                                "translationZh": "...", 
                                "sampleExpressions": [...], 
                                "sampleSentences": [...]} 
                                    ]
                                }
                            ]} 
                        Respond with JSON only. No comments, no Markdown, no extra text. 
                        Important: make sure you provide comprehensive list of dictionaryInfo word senses 
                        and sampleExpressions and sentences. When it's a list field, provide multiple if applicable 
                        in the sense of an official dictionary, rather than providing just one.  
                        Your response should strictly follow the name and structure in the json example given 
                        in prompt so the service could parse into java object.
                        """, word
        );
    }
    private static final String SYSTEM_PROMPT = """
    You are a English-Chinese dictionary assistant for an actual dictionary applications service.
    Make sure to be comprehensive and professional as if your response comes from a comprehensive authorized 
    dictionary api.
    Each word has a list of dictionaryInfo, each dictionary info has a string partOfSpeech, 
    a pronunciation and a list of wordSense,
    each pronunciation has string of uk and us field. 
    Each wordSense has definitionEn which is the english definition of the corresponding
    word sense followed by its Chinese translation, translationZh is a string of the word sense' direct translation,
    sampleExpressions and sampleSentences are list of strings of examples.
    Sample expressions, and sentences should be followed with their Chinese translations in one string.
    Do not confuse the translationZh with the translation for the definitionEn,
    translationZh is the word translation corresponding to the definitionEn,
    rather than the direct translation of definitionEn, 
    the direct translation of definitionEn should be in the definitionEn field
    following the english definition in one string.
    """;
}
