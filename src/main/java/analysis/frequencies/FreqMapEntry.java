package main.java.analysis.frequencies;

import java.util.*;

/**
 * <p>A FreqMapEntry is an entry of a map containing the author name, the title of the manuscript and the map
 * of data extracted from the analysis.</p>
 */
public class FreqMapEntry implements Comparable {
    private String author;
    private String title;
    private HashMap<String, Float> frequencies = new HashMap<>();
    private ArrayList<CommonWord> highestFrequencyList = new ArrayList<>();

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHighestFrequencyList(ArrayList<CommonWord> highestFrequencyList) {
        this.highestFrequencyList = highestFrequencyList;
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

    public void buildTopTen() {
        Collections.sort(this.highestFrequencyList, new Comparator<CommonWord>() {
            @Override
            public int compare(CommonWord commonWord, CommonWord t1) {
                return commonWord.compareTo(t1);
            }
        });

        this.setHighestFrequencyList(new ArrayList<>(this.highestFrequencyList.subList(0, 10)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, frequencies);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\nENTRY: " + this.author + " " + this.title + "\n");
        for (String field : this.frequencies.keySet()) {
            str.append("\t");
            str.append(field).append("=").append(this.frequencies.get(field)).append("\n");
        }

        str.append("------------------\n");
        for (CommonWord c : this.highestFrequencyList) {
            str.append("\t").append(c).append("\n");
        }
        return str.toString();

    }

    public boolean isUnknown() {
        return this.author.contains("unknown");
    }

    public boolean isGlobal() {
        return this.title.contains("global");
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
