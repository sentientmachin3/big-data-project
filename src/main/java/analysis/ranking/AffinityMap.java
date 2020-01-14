package main.java.analysis.ranking;


import main.java.analysis.frequencies.CommonWord;
import main.java.analysis.frequencies.FreqMap;
import main.java.analysis.frequencies.FreqMapEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Class generating an affinity map between two authors. </p>
 * <p>The class contains data to compare two authors using the information extracted from
 * the previous analysis. The comparison proceeds by calculating a delta for each field each author's map.
 * </p>
 */
public class AffinityMap implements Comparable {
    private String author;
    private String unknown;
    private HashMap<String, Double> map;

    /**
     * Generates an instance with empty comparison map.
     *
     * @param auth the known author's name.
     * @param unk  the unknown author's name.
     */
    public AffinityMap(String auth, String unk) {
        this.author = auth;
        this.unknown = unk;
        this.map = new HashMap<>();
    }

    public String getAuthor() {
        return author;
    }

    public String getUnknown() {
        return unknown;
    }

    public boolean isComparable() {
        // check if the text's lengths are comparable
        FreqMapEntry known = FreqMap.INSTANCE.getGlobalEntryByAuthor(this.author);
        FreqMapEntry unknown = FreqMap.INSTANCE.getGlobalEntryByAuthor(this.unknown);

        if (unknown.getFrequencies().get("nwords") < (known.getFrequencies().get("nwords") / 2)) {
            return false;
        }

        for (String s: this.map.keySet()) {
            if (!s.equals("nwords") && !s.equals("avg_period_length") && !s.equals("periods")) {
                if (this.map.get(s) > 0.15f) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Appends a field and its relative delta to the map.
     *
     * @param field the field name.
     * @param delta the calculated delta.
     */
    public void append(String field, double delta) {
        this.map.put(field, delta);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String key : this.map.keySet()) {
            sb.append(this.author).append("-").append(this.unknown).append("-").append(key).append("=").append(this.map.get(key)).append("\n");
        }

        return sb.toString();
    }


    @Override
    public int compareTo(Object o) {
        AffinityMap other = (AffinityMap) o;
        ArrayList<String> allowedFields = new ArrayList<>(Arrays.asList("articles", "verbs", "pronouns", "commas", "conjunctions", "prepositions"));

        if (this.map.get("avg_period_length").intValue() == other.map.get("avg_period_length").intValue()) {
            int lowerFields = 0;
            for (String key : allowedFields) {
                if (this.map.get(key) < other.map.get(key)) {
                    lowerFields++;
                }

                if (lowerFields > 3) {
                    return -1;
                } else {
                    return 1;
                }
            }

        } else if (this.map.get("avg_period_length").intValue() < other.map.get("avg_period_length").intValue()) {
            return -1;
        }

        return 1;

    }
}
