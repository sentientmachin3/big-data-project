package main.java;

import java.util.HashMap;

public class FreqMap {
    private HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

    public void append(String author, HashMap<String, Integer> mappings) {
        map.put(author, mappings);
    }

    void setValue(String author, String field, int value) {
        map.get(author).put(field, value);
    }

    @Override
    public String toString() {
        StringBuilder tostr = new StringBuilder();
        for (String auth : this.map.keySet()) {
            for (String field : this.map.get(auth).keySet())
                tostr.append(auth).append("-").append(field).append("=").append(this.map.get(auth).get(field)).append("\n");
        }

        return tostr.toString();
    }
}
