package me.mpearso.twitter.data.text;

import me.mpearso.twitter.data.DataFile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TextFile extends DataFile {

    protected LinkedList<String> lines = new LinkedList<>();

    public TextFile(String path, String fileName) {
        super(path, fileName, Extension.TXT);
    }

    @Override
    protected void init() {
        if(lines == null)
            lines = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {

            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                System.out.println("Read: " + line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String line : getLines()) {
            System.out.println("Cached: " + line);
        }
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFile()))){
            for(String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> getLines() {
        return lines;
    }

    public void addString(String string) {
        this.lines.add(string);
    }

    public void setLines(List<String> stringList) {
        this.lines = new LinkedList<>(stringList);
    }
}
