package main.java;

import java.io.BufferedReader;
import java.util.HashMap;

public class RatiosMap {
    private HashMap<String, Float> map;

    RatiosMap(BufferedReader reader) {
        map = new HashMap<>();
        HashMap<String, Integer> temp = new HashMap<>();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                temp.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (String s : temp.keySet()) {
            if (!(s.equals("nwords")))
                map.put(s, ((float)temp.get(s) / (float)temp.get("nwords")));
        }
    }

    HashMap<String, Float> getMap() {
        return map;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (String s : map.keySet()) {
            ret.append(s).append(": ").append(map.get(s)).append("\n");
        }

        return ret.toString();
    }
}
