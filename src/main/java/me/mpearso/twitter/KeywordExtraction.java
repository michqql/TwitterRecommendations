package me.mpearso.twitter;

import me.mpearso.twitter.data.text.ValuesTextFile;

import java.util.*;
import java.util.regex.Pattern;

public class KeywordExtraction {

    // Removes all characters that aren't a-z, A-Z or a whitespace
    private final static Pattern style = Pattern.compile("[^a-zA-Z ]");

    // Splits a text by whitespaces
    private final static Pattern splitter = Pattern.compile("( )+");

    private ValuesTextFile stopwords = new ValuesTextFile("files", "stopwords", ",");

    // A hashmap with all key terms and the times they appear across multiple texts
    private final LinkedHashMap<String, Integer> termCountMap = new LinkedHashMap<>();

    // Keeping track of how many keywords we have encountered across multiple texts
    private int termCounter = 0;

    public KeywordExtraction() {}

    /**
     * Extracts the most frequent terms in a text
     * Ignores english stopwords
     * @param text - the text to extract keywords from
     */
    public KeywordExtraction extract(String text) {
        int importantWordCounter = 0; // Number of terms that we care about (not counting stopwords)
        List<String> exclude = new ArrayList<>(); // Terms that have already been processed
        List<String> stopwords = this.stopwords.getValues(); // English stopwords to also exclude

        // The text split into an array, excluding punctuation
        // and converts whole text to lowercase
        String[] words = text.replaceAll(style.pattern(), "").toLowerCase().split(splitter.pattern());

        // Loop through the words in the text
        for(int i = 0; i < words.length; i++) {
            String term = words[i]; // The term we are comparing

            // Check if we have already compared this term
            // or if this term is a stopword (if so, we can skip this term)
            if(exclude.contains(term) || stopwords.contains(term))
                continue;

            // Counter of how many times this word appears
            int count = 1;

            // Loop through word array again, counting how many times it has occurred
            for(int j = 0; j < words.length; j++) {

                // If (i == j) then we are at the same index in both loops,
                // we can skip this because count starts at 1 (already counted the term)
                if(i == j) continue;

                // Check if the terms are equal, if so increment the counter
                if(term.equals(words[j]))
                    count++;
            }

            // Add the term to the excluded list, so that we don't count this word again
            exclude.add(term);

            // Add this term to the hashmap of key terms
            int cachedCount = termCountMap.getOrDefault(term, 0);
            this.termCountMap.put(term, cachedCount + count);

            // Increment the number of important words by count
            importantWordCounter += count;
        }

        // Add our local keyword counter to the global counter
        this.termCounter += importantWordCounter;

        // Returns this (self) object, so that other methods can be called inline
        return this;
    }

    public void print() {
        for(Map.Entry<String, Integer> entry : this.termCountMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + (float) entry.getValue() / this.termCounter);
        }
    }
}
