package me.mpearso.twitter.keyword;

import me.mpearso.twitter.io.impl.text.SeparatedValueTextFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleKeywordExtractionAlgorithm {

    // Removes all characters that aren't a-z, A-Z or a whitespace
    private final static Pattern CHARACTERS = Pattern.compile("[^a-zA-Z ]");

    // Splits a text by whitespaces
    private final static Pattern SPLITTER = Pattern.compile("( )+");

    // Stopwords that the algorithm will exclude
    // List should be unmodifiable - elements cannot be added or removed
    private final List<String> stopwords;

    public SimpleKeywordExtractionAlgorithm() {

        // Load stopwords from file
        SeparatedValueTextFile csvFile = new SeparatedValueTextFile("files/stopwords.txt", ",");
        this.stopwords = Collections.unmodifiableList(csvFile.getValues()); // Ensure that the list is unmodifiable
    }

    public void extract(String text) {
        List<String> exclude = new ArrayList<>(); // Terms that have already been processed

        // Split the text into an array. Each element contains a single term
        // All non-alphabetic characters are removed
        // All terms are converted to lowercase
        String[] words = text.replaceAll(CHARACTERS.pattern(), "").toLowerCase().split(SPLITTER.pattern());

        // Loop through the words in the text
        for(int i = 0; i < words.length; i++) {
            String term = words[i]; // The term we are comparing

            // Check if we have already compared this term
            // or if this term is a stopword (if so, we can skip this term)
            if(exclude.contains(term) || stopwords.contains(term))
                continue;

            System.out.println(term);
            exclude.add(term);
        }
    }
}
