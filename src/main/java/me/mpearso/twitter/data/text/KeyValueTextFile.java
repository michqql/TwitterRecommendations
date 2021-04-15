package me.mpearso.twitter.data.text;

import java.util.HashMap;

public class KeyValueTextFile extends TextFile {

    private final String splitterRegex;
    protected final HashMap<String, String> values;

    public KeyValueTextFile(String path, String fileName, String splitter) {
        super(path, fileName);

        this.splitterRegex = splitter;
        this.values = new HashMap<>();
        parse();
    }

    public void parse() {
        for(String line : getLines()) {
            String[] keyValue = line.split(splitterRegex, 2);
            if(keyValue.length >= 2) {
                values.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public void put(String key, String value) {
        this.addLine(key + splitterRegex + value);
        this.values.put(key, value);
    }

    private String getRawData(String key) {
        return values.get(key);
    }

    public String getString(String key) {
        return getRawData(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getRawData(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(getRawData(key));
    }

    public long getLong(String key) {
        return Long.parseLong(getRawData(key));
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
