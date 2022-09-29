package me.mpearso.twitter.keyword;

import me.mpearso.twitter.io.impl.text.SeparatedValueTextFile;

import java.util.*;
import java.util.regex.Pattern;

public class SimpleKeywordExtractionAlgorithm {

    // Line breaks and tabs
    private final static Pattern LINE_BREAKS = Pattern.compile("[\\n\\t]");

    // Removes all characters that aren't a-z, A-Z or a whitespace
    private final static Pattern CHARACTERS = Pattern.compile("[^a-zA-Z ]");

    // Splits a text by whitespaces
    private final static Pattern SPLITTER = Pattern.compile("( )+");

    // Stopwords that the algorithm will exclude
    // List should be unmodifiable - elements cannot be added or removed
    private final List<String> stopwords;

    // A hashmap with all key terms and the times they appear across multiple texts
    private final HashMap<String, Integer> termCountMap = new HashMap<>();

    // Keeping track of how many keywords we have encountered across multiple texts
    private int termCounter = 0;

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
        String[] words = text.replaceAll(LINE_BREAKS.pattern(), " ")
                .replaceAll(CHARACTERS.pattern(), "")
                .toLowerCase().split(SPLITTER.pattern());

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
            for(int j = (i + 1); j < words.length; j++) {
                // j can never be less than i as we start counting at i + 1
                // This is because words[i] is the first time we have encountered this term
                // therefore there is no point looping through the words before this index
                // as we know they cannot be this term

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
     * @return linked hash map (term -> frequency)
     */
    public LinkedHashMap<String, Float> getOrderedTermFrequencies() {
        // LinkedHashMap as these entries are ordered
        final LinkedHashMap<String, Float> terms = new LinkedHashMap<>();

        // TODO: Replace bubble sort with more efficient sorting algorithm
        // Copy the term-occurrence map so we are not making changes directly to it
        final HashMap<String, Integer> termMapCopy = new HashMap<>(this.termCountMap);
        termMapCopy.entrySet().stream()
                .sorted((term1, term2) -> term2.getValue() - term1.getValue()) // Sort the terms by weight (high to low)
                .forEach(entry -> terms.put(entry.getKey(), (float) entry.getValue() / this.termCounter)); // Collect this to a map

        return terms;
    }

    public void clearTerms() {
        this.termCounter = 0;
        this.termCountMap.clear();
    }
}
