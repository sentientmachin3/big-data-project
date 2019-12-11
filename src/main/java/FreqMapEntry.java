package main.java;

import java.util.*;

/**
 * <p>A FreqMapEntry is an entry of a map containing the author name, the title of the manuscript and the map
 * of data extracted from the analysis.</p>
 */
public class FreqMapEntry {
    private String author;
    private String title;
    private HashMap<String, Float> frequencies = new HashMap<>();
    private ArrayList<CommonWord> highestFrequencyList =new ArrayList<>();

    /**
     * Constructor for FreqMapEntry class.
     * <b>Note:</b> this constructor generates an empty map for the data.
     *
     * @param author the author name.
     * @param title  the title of the writing.
     */
    public FreqMapEntry(String author, String title) {
        this.author = author;
        this.title = title;
    }

    /**
     * <p> Constructor for a FreqMapEntry instance. This constructor takes as argument a field name
     * and the value to be assigned to that field, most likely to be used to updated a map.</p>
     *
     * @param author the author's name.
     * @param title  the title of the manuscript.
     * @param field  the field name.
     * @param value  the value to be assigned to the field.
     */
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

    public ArrayList<CommonWord> getHighestFrequencyList() {
        return highestFrequencyList;
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

    public void addCommonWord(CommonWord commonWord) {
        this.highestFrequencyList.add(commonWord);
    }

//    public void updateCommonWord(String word, float value) {
//        for (CommonWord w: this.getHighestFrequencyList()) {
//            if (w.getWord().equals(word)) {
//                w.setValue(value);
//            }
//        }
//    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, frequencies);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String field : this.frequencies.keySet()) {
            str.append(this.author).append("-").append(this.title).append("-").append(field).append("=").append(this.frequencies.get(field)).append("\n");
        }
        return str.toString();

    }

    public boolean isUnknown() {
        return this.author.contains("unknown");
    }

    public boolean isGlobal() {
        return this.title.contains("global");
    }
}
