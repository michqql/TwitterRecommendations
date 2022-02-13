package me.mpearso.twitter;

import me.mpearso.twitter.io.text.ValuesTextFile;

import java.util.*;
import java.util.regex.Pattern;

public class KeywordExtraction {

    // Removes all characters that aren't a-z, A-Z or a whitespace
    private final static Pattern characters = Pattern.compile("[^a-zA-Z ]");

    // Splits a text by whitespaces
    private final static Pattern splitter = Pattern.compile("( )+");

    // A file listing all english + chosen stopwords
    private final ValuesTextFile stopwords = new ValuesTextFile("files/stopwords.txt", ",");

    // A hashmap with all key terms and the times they appear across multiple texts
    private final LinkedHashMap<String, Integer> termCountMap = new LinkedHashMap<>();

    // Keeping track of how many keywords we have encountered across multiple texts
    private int termCounter = 0;

    public KeywordExtraction() {}

    /**
     * Extracts the most frequent terms in a text
     * Ignores english stopwords as defined by the stopwords object
     * @param text - the text to extract keywords from
     */
    public KeywordExtraction extract(String text) {
        List<String> exclude = new ArrayList<>(); // Terms that have already been processed
        List<String> stopwords = this.stopwords.getValues(); // English stopwords to also exclude

        // The text split into an array, excluding punctuation
        // and converts whole text to lowercase
        String[] words = text.replaceAll(characters.pattern(), "").toLowerCase().split(splitter.pattern());

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
                if(term.equalsIgnoreCase(words[j]))
                    count++;
            }

            // Add the term to the excluded list, so that we don't count this word again
            exclude.add(term);

            // Add this term to the hashmap of key terms
            int cachedCount = termCountMap.getOrDefault(term, 0);
            this.termCountMap.put(term, cachedCount + count);

            // Increment the number of important words by count to our global counter
            this.termCounter += count;
        }

        // Returns this (self) object, so that other methods can be called inline
        return this;
    }

    /**
     * Returns a hashmap with each unique key term and the frequency (as a float 0-1)
     * of how often it appears within the texts
     * @return hashmap(term, frequency)
     */
    public HashMap<String, Float> getTermFrequencies() {
        final HashMap<String, Float> terms = new HashMap<>();

        // Loop through all entries in the global term counter map
        // and convert this count to a frequency float by dividing by the
        // global term counter and storing this in the local map
        this.termCountMap.forEach((term, count) -> terms.put(term, (float) count / this.termCounter));
        return terms;
    }

    /**
     * Returns a linked hashmap with each unique key term and the frequency (as a float 0-1)
     * of how often it appears within the texts
     *
     * Hashmap is ordered depending on frequency (more weighting for higher frequency, high to low)
     * @return linked hashmap(term, frequency)
     */
    public LinkedHashMap<String, Float> getOrderedTermFrequencies() {
        final LinkedHashMap<String, Float> terms = new LinkedHashMap<>();

        // Compare all entries in the global term counter map
        // to sort them by their count, then loop through this
        // new order and convert the count to a frequency float
        // by dividing by the global term counter and storing this
        // in the local map
        this.termCountMap.entrySet().stream()
                .sorted((term1, term2) -> term2.getValue() - term1.getValue()) // Sort the terms by weight (high to low)
                .forEach(entry -> terms.put(entry.getKey(), (float) entry.getValue() / this.termCounter)); // Collect this to a map

        return terms;
    }

    /**
     * Function to print term and frequency to the console (or default output stream)
     * Prints in order (high to low)
     */
    public void print() {
        for(Map.Entry<String, Float> entry : getOrderedTermFrequencies().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
