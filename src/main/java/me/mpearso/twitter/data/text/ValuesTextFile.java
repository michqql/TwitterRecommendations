package me.mpearso.twitter.data.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValuesTextFile extends TextFile {

    private final String SPLITTER_REGEX;
    private List<String> values;

    public ValuesTextFile(String path, String fileName, String splitter) {
        super(path, fileName);

        this.SPLITTER_REGEX = splitter;
        parse();
    }

    private void parse() {
        values = new ArrayList<>();
        for(String line : lines) {
            values.addAll(Arrays.asList(line.split(SPLITTER_REGEX)));
        }
    }

    public List<String> getValues() {
        return values;
    }
}
