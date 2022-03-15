package me.mpearso.twitter.io.impl.text;

import me.mpearso.twitter.io.impl.TextFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Annotate class
public class SeparatedValueTextFile extends TextFile {

    private final String separatorRegex;
    private List<String> values;

    public SeparatedValueTextFile(String path, String separatorRegex) {
        super(path);

        this.separatorRegex = separatorRegex;
        parse();
    }

    private void parse() {
        if(values == null)
            values = new ArrayList<>();

        for(String line : lines) {
            String[] valuesInLine = line.split(separatorRegex);
            values.addAll(Arrays.asList(valuesInLine));
        }
    }

    public List<String> getValues() {
        if(values == null)
            return (values = new ArrayList<>());

        return values;
    }
}
