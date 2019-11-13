package main.java;

import java.util.HashMap;
import java.util.Objects;

public class FreqMapEntry {
    private String author;
    private String title;
    // always generates an empty map
    private HashMap<String, Float> frequencies = new HashMap<>();

    public FreqMapEntry(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public FreqMapEntry(String author, String title, String field, float value) {
        this.author = author;
        this.title = title;
        this.frequencies.put(field, value);
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, Float> getFrequencies() {
        return frequencies;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FreqMapEntry) {
            FreqMapEntry entry = (FreqMapEntry) o;
            return Objects.equals(author, entry.author) &&
                    Objects.equals(title, entry.title) &&
                    Objects.equals(frequencies, entry.frequencies);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, frequencies);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for (String field : this.frequencies.keySet()) {
            str.append(this.author).append("-").append(this.title).append("-").append(field).append("=").append(this.frequencies.get(field)).append("\n");
        }
        return str.toString();

    }
}
