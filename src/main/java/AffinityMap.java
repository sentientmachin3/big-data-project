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

    @Override
    public String toString() {
        String init = this.author + "-" + this.unknown + "-";
        StringBuilder sb = new StringBuilder("");
        for (String f:map.keySet()) {
            sb.append(init).append(f).append("=").append(this.map.get(f)).append("\n");
        }

        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
