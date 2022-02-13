package me.mpearso.twitter.io.text;

import me.mpearso.twitter.io.FileWrapper;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TextFile extends FileWrapper {

    protected LinkedList<String> lines;

    public TextFile(String path) {
        super(path);
    }

    @Override
    protected void load() {
        if(lines == null)
            lines = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {

            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public void addLine(String string) {
        this.addString(string);
    }

    public void setLines(List<String> stringList) {
        this.lines = new LinkedList<>(stringList);
    }
}
