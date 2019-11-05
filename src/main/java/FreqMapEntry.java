package main.java;

import java.util.HashMap;

public class FreqMapEntry {
    private String author;
    private String text;
    private HashMap<String, Float> frequencies = new HashMap<>();

    public FreqMapEntry(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public FreqMapEntry(String author, String title, String field, float value) {
        this.author = author;
        this.text = title;
        this.frequencies.put(field, value);
    }

    public void setText(String title) {
        this.text = title;
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
