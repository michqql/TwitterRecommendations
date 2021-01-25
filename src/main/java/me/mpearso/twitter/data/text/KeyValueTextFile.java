package me.mpearso.twitter.data.text;

import java.util.HashMap;

public class KeyValueTextFile extends TextFile {

    private final String splitterRegex;
    protected final HashMap<String, String> values = new HashMap<>();

    public KeyValueTextFile(String path, String fileName, String splitter) {
        super(path, fileName);

        for(String line : getLines()) {
            System.out.println("Cached2: " + line);
        }

        this.splitterRegex = splitter;
        parse();
    }

    public void parse() {
        for(String line : getLines()) {
            String[] keyValue = line.split(splitterRegex, 2);
            if(keyValue.length == 2) {
                values.put(keyValue[0], keyValue[1]);
            }
        }
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

    public int getInt(String key) {
        return Integer.parseInt(getRawData(key));
    }
}
