package main.java;

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
        String init = this.author + "-" + this.unknown + "-";
        StringBuilder sb = new StringBuilder();
        for (String f : map.keySet()) {
            sb.append(init).append(f).append("=").append(this.map.get(f)).append("\n");
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    @Override
    public int compareTo(Object o) {
        AffinityMap obj = (AffinityMap) o;
        double thisAvg = 0.0;
        double otherAvg = 0.0;
        for (String field : this.map.keySet()) {
            if (!field.equals("avg_period_length")){
                thisAvg += this.map.get(field);
                otherAvg += obj.map.get(field);
            }
        }

        thisAvg /= 5;
        otherAvg /= 5;

        if (thisAvg < otherAvg) {
            return -1;
        } else if (thisAvg > otherAvg) {
            return 1;
        }

        return 0;
    }
}
