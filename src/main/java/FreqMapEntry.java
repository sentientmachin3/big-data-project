package main.java;

import java.util.HashMap;

public class FreqMapEntry {
    private String author;
    private String text;
    private HashMap<String, Float> frequencies;

    public FreqMapEntry(String author, String text) {
        this.author = author;
        this.text = text;
        this.frequencies = new HashMap<>();
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public HashMap<String, Float> getFrequencies() {
        return frequencies;
    }
}
