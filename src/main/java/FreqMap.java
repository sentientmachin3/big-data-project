package main.java;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FreqMap implements Map<String, HashMap<String, Float>> {
    private HashSet<FreqMapEntry> entries;

    FreqMap() {
        this.entries = new HashSet<>();
    }


    Set<String> getAuthors() {
        return this.keySet();
    }

    private void setValue(String author, String field, float value) {
        this.get(author).put(field, value);
    }

    private void addAuthorWithEmptyMap(String author) {
        if (!this.containsKey(author))
            this.put(author, new HashMap<String, Float>());
    }

    @Override
    public String toString() {
        StringBuilder tostr = new StringBuilder();
        for (String auth : this.keySet()) {
            for (String field : this.get(auth).keySet())
                tostr.append(auth).append("-").append(field).append("=").append(this.get(auth).get(field)).append("\n");
        }

        return tostr.toString();
    }

    private void calculateFrequencies() {
        for (String auth : this.keySet()) {
            for (String field : this.get(auth).keySet()) {
                if (field.equals("article") || field.equals("conjunction") || field.equals("preposition")) {
                    float upval = this.get(auth).get(field) / this.get(auth).get("nwords");
                    this.get(auth).put(field, upval);
                }
            }
            this.get(auth).put("avg_period_length", this.get(auth).get("nwords") / this.get(auth).get("periods"));
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

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        for (FreqMapEntry entry: this.entries) {
            if (o.equals(entry.getAuthor()))
                return true;
        }

        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        for (FreqMapEntry entry: this.entries) {
            if (entry.getFrequencies().containsValue(o))
                return true;
        }

        return false;
    }

    @Override
    public HashMap<String, Float> get(Object o) {
        for (FreqMapEntry entry: entries) {
            if (entry.getAuthor().equals(o))
                return entry.getFrequencies();
        }

        return null;
    }

    @Override
    public HashMap<String, Float> put(String o, HashMap<String, Float> o2) {
        return null;
    }

    @Override
    public HashMap<String, Float> remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends HashMap<String, Float>> map) {}

    @Override
    public void clear() {
        this.entries.clear();
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> names = new HashSet<>();
        for (FreqMapEntry entry: entries) {
            names.add(entry.getAuthor());
        }

        return names;
    }

    @Override
    public Collection<HashMap<String, Float>> values() {
        HashSet<HashMap<String, Float>> set = new HashSet<>();
        for (FreqMapEntry entry: entries) {
            set.add(entry.getFrequencies());
        }

        return set;
    }

    @Override
    public Set<Entry<String, HashMap<String, Float>>> entrySet() {
        HashSet<Entry<String, HashMap<String, Float>>> set = new HashSet<>();
        for (final FreqMapEntry entry: entries) {
            set.add(new Entry<String, HashMap<String, Float>>() {
                @Override
                public String getKey() {
                    return entry.getAuthor();
                }

                @Override
                public HashMap<String, Float> getValue() {
                    return entry.getFrequencies();
                }

                @Override
                public HashMap<String, Float> setValue(HashMap<String, Float> stringFloatHashMap) {
                    return stringFloatHashMap;
                }
            });
        }

        return set;
    }
}
