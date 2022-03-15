package me.mpearso.twitter;

import me.mpearso.twitter.io.impl.text.SeparatedValueTextFile;

public class Main {

    public static void main(String[] args) {
        // Test text file reads and writes properly
        SeparatedValueTextFile textFile = new SeparatedValueTextFile("file/test.txt", ",");

        // Read test
        // Lambda expression to print each string to console
        textFile.getValues().forEach(System.out::println);

        // Write test
        textFile.getLines().clear();
        textFile.getLines().add("testing,123,testing,123");
        textFile.save();
    }
}
