package main.java;

import java.util.HashMap;
import java.util.Map;

public class AffinityMap implements Comparable {
    private String author;
    private String unknown;
    private HashMap<String, Double> map;

    public AffinityMap(String auth, String unk) {
        this.author = auth;
        this.unknown = unk;
        this.map = new HashMap<>();
    }

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
        AffinityMap rec = (AffinityMap) o;
        HashMap<Integer, Integer> count = new HashMap<>();
        count.put(-1, 0);
        count.put(0, 0);
        count.put(1, 0);

        // count for compare values
        for (String s : this.map.keySet()) {
            if (this.map.get(s) < rec.map.get(s)) {
                count.put(-1, count.get(-1) + 1);
            } else if (rec.map.get(s) < this.map.get(s)) {
                count.put(1, count.get(1) + 1);
            } else {
                count.put(0, count.get(0) + 1);
            }
        }

        // max on compare counts
        Map.Entry<Integer, Integer> max = null;
        int intmax = 0;
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() > intmax) {
                intmax = entry.getValue();
                max = entry;
            }
        }
        assert max != null;
        return max.getKey();

    }
}
