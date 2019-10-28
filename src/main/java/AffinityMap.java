package main.java;

import java.util.HashMap;

public class AffinityMap {
    private String author;
    private String unknown;
    private HashMap<String, Float> map;

    public AffinityMap(String auth, String unk, HashMap<String, Float> map) {
        this.author = auth;
        this.unknown = unk;
        this.map = map;
    }
}
