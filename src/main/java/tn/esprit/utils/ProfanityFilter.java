package tn.esprit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ProfanityFilter {
    private final Set<String> profanitySet;

    public ProfanityFilter() {
        profanitySet = new HashSet<>();
        loadProfanityWords();
    }

    private void loadProfanityWords() {
        try (InputStream is = getClass().getResourceAsStream("/profane_words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Trim whitespace and convert to lowercase
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    profanitySet.add(line);
                }
            }
            System.out.println("Loaded " + profanitySet.size() + " profane words");
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading profanity list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean containsProfanity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // Convert to lowercase for case-insensitive matching
        String lowerText = text.toLowerCase();

        // Split text into words and check each word
        String[] words = lowerText.split("\\s+");
        for (String word : words) {
            // Remove common punctuation
            word = word.replaceAll("[.,!?]", "");
            if (profanitySet.contains(word)) {
                System.out.println("Profanity detected: " + word); // Debug line
                return true;
            }
        }

        // Check for partial matches and obfuscation
        for (String profaneWord : profanitySet) {
            if (lowerText.contains(profaneWord)) {
                System.out.println("Profanity detected (partial match): " + profaneWord); // Debug line
                return true;
            }
        }

        return false;
    }

    public String getProfanityMessage() {
        return "This message contains inappropriate content and has been blocked.";
    }
}