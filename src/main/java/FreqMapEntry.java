package main.java;

import java.util.HashMap;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FreqMapEntry entry = (FreqMapEntry) o;
        return Objects.equals(author, entry.author) &&
                Objects.equals(text, entry.text) &&
                Objects.equals(frequencies, entry.frequencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text, frequencies);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for (String field: this.frequencies.keySet()) {
            str.append(this.author).append("-").append(this.text).append("-").append(field).append("=").append(this.frequencies.get(field)).append("\n");
        }
        return str.toString();

    }
}
