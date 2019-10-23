package main.java;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FreqMap {
    private HashMap<String, HashMap<String, Float>> map;

    FreqMap() {
        this.map = new HashMap<>();
    }

    private void setValue(String author, String field, float value) {
        map.get(author).put(field, value);
    }

    private void addAuthorWithEmptyMap(String author) {
        if (!this.map.containsKey(author))
            this.map.put(author, new HashMap<String, Float>());
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

    private void calculateFrequencies() {
        for (String auth : map.keySet()) {
            for (String field : map.get(auth).keySet()) {
                if (field.equals("articles") || field.equals("conjunctions")) {
                    float upval = map.get(auth).get(field) / map.get(auth).get("nwords");
                    map.get(auth).put(field, upval);
                }
            }
            this.map.get(auth).put("avg_period_length", map.get(auth).get("nwords") / map.get(auth).get("periods"));
        }
    }

    void toFile(FileSystem fs, Path path) throws IOException {
        FSDataOutputStream outputStream = fs.create(path);
        outputStream.writeBytes(this.toString());
        outputStream.flush();
        outputStream.close();
    }

    void fromFile(FileSystem fs, Path path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));

        // value parsing
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String author = line.split(".txt")[0];
            this.addAuthorWithEmptyMap(author);

            String field = line.split(".txt")[1].split("\t")[0];
            float value = Float.parseFloat(line.split(".txt")[1].split("\t")[1]);

            this.setValue(author, field, value);
        }

        this.calculateFrequencies();
    }
}
