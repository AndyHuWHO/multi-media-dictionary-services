package com.word.wordservice.client;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalWordValidationService {
    private Set<String> englishWords = new HashSet<>();

    @PostConstruct
    public void init() {
        if (!loadFromSerializedFile()) {
            loadFromTextFile();
        }
    }

    private boolean loadFromSerializedFile() {
        try(ObjectInputStream in = new ObjectInputStream(
                new ClassPathResource("data/dictionary.ser").getInputStream())
        ) { Object obj = in.readObject();
            if (obj instanceof Set) {
                englishWords = (Set<String>) obj;
                System.out.println("Loaded word list from serialized file.");
                return true;
            } else {
                System.err.println("Serialized file is not a Set<String>");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to load from .ser: " + e.getMessage());
            return false;
        }
    }

    private void loadFromTextFile() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/english.txt").getInputStream(),
                        StandardCharsets.UTF_8))
        ) {
            englishWords = bufferedReader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            System.out.println("Loaded word list from text file.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load word list from text file.", e);
        }
    }

    public boolean isValidWord(String word) {
        return englishWords.contains(word.toLowerCase());
    }
}
