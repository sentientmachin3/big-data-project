package main.java;


import java.util.ArrayList;
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
    private FreqMap freqMap;
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

    public AffinityMap( String auth, String unk, FreqMap freqMap) {
        this.author = auth;
        this.unknown = unk;
        this.map = new HashMap<>();
        this.freqMap = freqMap;
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

    void setFreqMap(FreqMap map) {
        this.freqMap = map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String field : this.map.keySet()) {
            sb.append(this.author).append("-").append(this.unknown).append("-").append(field).append("-")
                    .append(this.map.get(field)).append("\n");
        }

        for (CommonWord c : this.freqMap.getCWSFromAuthor(this.author)) {
            if (this.freqMap.getCWSFromAuthor(this.unknown).contains(c)) {
                sb.append(this.author).append("-").append(this.unknown).append("-").append("common").append("-")
                        .append(c.getWord()).append("\n");
            }
        }

        return sb.toString();
    }


    @Override
    public int compareTo(Object o) {
        AffinityMap obj = (AffinityMap) o;
        // TODO: end method
        return 0;
    }

//    private ArrayList<CommonWord> cwIntersection() {
//        FreqMapEntry known = null;
//        FreqMapEntry unknown = null;
//
//        for (FreqMapEntry e : this.freqMap.getEntries()) {
//            if (e.getAuthor().equals(this.author)) {
//                known = e;
//            } else if (e.getAuthor().equals(this.unknown)) {
//                unknown = e;
//            }
//        }
//
//        assert unknown != null && known != null;
//        ArrayList<CommonWord> commonWords = new ArrayList<>();
//        for (CommonWord c : known.getHighestFrequencyList()) {
//            if (unknown.getHighestFrequencyList().contains(c)) {
//                commonWords.add(c);
//            }
//        }
//
//        return commonWords;
//    }


}
