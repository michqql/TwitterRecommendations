package me.mpearso.twitter.io.impl;

import com.google.gson.*;
import me.mpearso.twitter.io.FileWrapper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonFile extends FileWrapper {

    public final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonElement element;

    public JsonFile(String path) {
        super(path);
    }

    /**
     * Reads the json object from file
     */
    @Override
    protected void load() {
        try {
            // Parses the string (in json format) from the file and turns it into an object
            // Parsing is handled by Gson library
            this.element = JsonParser.parseReader(new FileReader(file));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the json object to file
     */
    @Override
    public void save() {
        try {
            // Takes the json object and writes it to the file using Gson library
            FileWriter writer = new FileWriter(file);
            GSON.toJson(element, writer);

            // flush() method is important as it commits/writes the data to the file
            writer.flush();
            writer.close();
        } catch(JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement getElement() {
        return element;
    }

    public JsonObject getAsJsonObject() {
        return element.getAsJsonObject();
    }

    public JsonArray getAsJsonArray() {
        return element.getAsJsonArray();
    }

    public void setElement(JsonElement element) {
        this.element = element;
    }
}
