package main.java;

import java.util.HashMap;

public class AffinityMap {
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
}
