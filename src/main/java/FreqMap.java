package main.java;

import java.util.HashMap;

public class FreqMap {
    private HashMap<String, HashMap<String, Float>> map;

    FreqMap() {
        this.map = new HashMap<>();
    }

    void setValue(String author, String field, float value) {
        map.put(author, new HashMap<String, Float>());
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

    void calculateFrequencies() {
        for (String auth : map.keySet()) {
            for (String field : map.get(auth).keySet()) {
                if (!field.equals("nwords")) {
                    float upval = map.get(auth).get(field) / map.get(auth).get("nwords");
                    map.get(auth).put(field, upval);
                }
            }
        }
    }

}
