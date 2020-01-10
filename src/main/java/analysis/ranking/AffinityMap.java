package main.java.analysis.ranking;


import main.java.analysis.frequencies.CommonWord;
import main.java.analysis.frequencies.FreqMap;

import java.util.HashMap;

/**
 * <p>Class generating an affinity map between two authors. </p>
 * <p>The class contains data to compare two authors using the information extracted from
 * the previous analysis. The comparison proceeds by calculating a delta for each field each author's map.
 * </p>
 */
public class AffinityMap {
    private String author;
    private String unknown;
    private HashMap<String, Double> map;
    private Ranking ranking;

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

    public AffinityMap(String auth, String unk, FreqMap freqMap) {
        this.author = auth;
        this.unknown = unk;
        this.map = new HashMap<>();
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void addRanking(Ranking r) {
        this.ranking = r;
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

        sb.append(this.unknown).append(":");
        for (String auth : this.ranking.getSortedRanking()) {
            sb.append(auth).append(" - ");
        }

        sb.deleteCharAt(sb.length()- 1);
        sb.append("\n");
        return sb.toString();
    }


}
