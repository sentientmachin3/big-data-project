package main.java;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.hash.Hash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class representing a FreqMapEntry instance. A FreqMap is a set of entries (instances of FreqMapEntry) containing,
 * for each author and each text, the results of the analysis of a single input file.
 */
public class FreqMap implements Map<String, HashMap<String, Float>> {
    private HashSet<FreqMapEntry> entries;

    /**
     * Empty constructor for an instance. Simply generates an empty set.
     */
    FreqMap() {
        this.entries = new HashSet<>();
    }

    public HashSet<FreqMapEntry> getEntries() {
        return this.entries;
    }

    @Override
    public String toString() {
        StringBuilder tostr = new StringBuilder();
        for (FreqMapEntry entry : entries) {
            tostr.append(entry.toString());
        }

        return tostr.toString();
    }

    /**
     * Method filling the frequency map with the frequencies of the fields needed.
     */
    private void calculateFrequencies() {
        // gets the first ten words
        for (FreqMapEntry entry : this.entries) {
            entry.buildTopTen();
        }

        // for each entry computes the frequency of articles, conjunctions and prepositions by dividing the counted words
        // by the total number of words

        for (FreqMapEntry entry : entries) {
            for (String field : entry.getFrequencies().keySet()) {
                if (field.equals("articles") || field.equals("conjunctions") || field.equals("prepositions") ||
                        field.equals("commas") || field.equals("pronouns") || field.equals("verbs")) {
                    float upval = entry.getFrequencies().get(field) / entry.getFrequencies().get("nwords");
                    entry.getFrequencies().put(field, upval);
                }
            }

            // calculates the most common words frequency on the total number of words
            for (CommonWord w : entry.getHighestFrequencyList()) {
                w.setValue(w.getValue() / entry.getFrequencies().get("nwords"));
            }

            // computes the average period length by dividing the total number of words by the number of periods.
            entry.getFrequencies().put("avg_period_length", entry.getFrequencies().get("nwords") /
                    entry.getFrequencies().get("periods"));
        }

        // call the method for global frequencies (average of author's parameters)
        for (String author : this.keySet()) {
            this.globalAuthorFrequency(author);
        }

    }

    /**
     * Generates a global frequency for each author by computing the average values for each field in the frequency map.
     * Substitutes all the entries of an author with a single entry with the same author name and "global" as title.
     *
     * @param author the author's name to compute the average of.
     */
    private void globalAuthorFrequency(String author) {
        FreqMapEntry global = new FreqMapEntry(author, "global");

        // init map to 0
        for (String field : entries.iterator().next().getFrequencies().keySet()) {
            global.getFrequencies().put(field, (float) 0);
        }

        // sums by field, entries are grouped by author
        ArrayList<FreqMapEntry> authorEntries = new ArrayList<>();
        HashSet<CommonWord> wordsPerAuthor = new HashSet<>();
        HashMap<String, Float> globalWordsPerAuthor = new HashMap<>();
        ArrayList<CommonWord> res = new ArrayList<>();


//        for (FreqMapEntry entry : this.entries) {
//            if (entry.getAuthor().equals(author)) {
//                authorEntries++;
//                for (String field : entry.getFrequencies().keySet()) {
//                    float upvalue = global.getFrequencies().get(field) + entry.getFrequencies().get(field);
//                    global.getFrequencies().put(field, upvalue);
//                }
//
//
//                for(CommonWord w : entry.getHighestFrequencyList()) {
//                    boolean contained = false;
//                    if(!globalCommon.isEmpty()) {
//                        for(CommonWord w0 : globalCommon) {
//                            if(w0.getWord().equals(w.getWord())) {
//                                contained = true;
//                                break;
//                            }
//                        }
//                    }
//
//                    if(!contained) {
//                        globalCommon.add(w);
//                    }
//                }
//
//                for(CommonWord w2 : entry.getHighestFrequencyList()) {
//                    for (CommonWord commonWord : globalCommon) {
//                        if (commonWord.getWord().equals(w2.getWord())) {
//                            float value = commonWord.getValue() + w2.getValue();
//                            commonWord.setValue(value);
//
//                        }
//                    }
//                }
//            }

        // merge words
        for (FreqMapEntry f : this.entries) {
            if (f.getAuthor().equals(author)) {
                wordsPerAuthor.addAll(f.getHighestFrequencyList());
            }
        }

        for (CommonWord c : wordsPerAuthor) {
            globalWordsPerAuthor.put(c.getWord(), 0.0f);
        }


        // collect entries from the same author
        for (FreqMapEntry e : this.entries) {
            if (e.getAuthor().equals(author)) {
                authorEntries.add(e);
            }
        }

        // average using number of entries
        for (String field : global.getFrequencies().keySet()) {
            global.getFrequencies().put(field, global.getFrequencies().get(field) / authorEntries.size());
        }

        // collect words avg
        for (FreqMapEntry entry : authorEntries) {
            for (CommonWord cw : entry.getHighestFrequencyList()) {
                globalWordsPerAuthor.put(cw.getWord(), globalWordsPerAuthor.get(cw.getWord()) + cw.getValue());
            }
        }

        for (String s : globalWordsPerAuthor.keySet()) {
            globalWordsPerAuthor.put(s, globalWordsPerAuthor.get(s) / authorEntries.size());
        }

        for (String s : globalWordsPerAuthor.keySet()) {
            res.add(new CommonWord(s, globalWordsPerAuthor.get(s)));
        }


//        for (CommonWord cw : globalCommon) {
//            System.out.println(cw);
//        }
//        System.out.println("-----------------------");
//
        global.setHighestFrequencyList(res);
        global.buildTopTen();
        this.entries.add(global);
    }

    /**
     * Writes to file this FreqMap.
     *
     * @param fs   the filesystem where the file is written.
     * @param path the path where the file is about to be saved.
     * @throws IOException if an IOException writing the file occurs.
     */
    void toFile(FileSystem fs, Path path) throws IOException {
        FSDataOutputStream outputStream = fs.create(path);
        outputStream.writeBytes(this.toString());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Fetches a FreqMap instance from the job output file. This method also calls the
     * calculateFrequencies() method.
     *
     * @param fs   the filesystem where the output file is located.
     * @param path the path where the file is located.
     * @throws IOException if an IOException reading the file occurs.
     */
    void fromFile(FileSystem fs, Path path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));

        // value parsing
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String author = line.split("-")[0];
            String title = line.split(".txt\\*")[0].substring(line.split(".txt\\*")[0].indexOf("-") + 1);
            ;
            String field = null;
            float value;
            if (!line.contains("commons")) {
                field = line.split(".txt\\*")[1].split("\t")[0];
                value = Float.parseFloat(line.split(".txt\\*")[1].split("\t")[1]);
                this.update(author, title, field, value);

            } else {
                // common words are formatted in a different way, so...
                CommonWord comWord = new CommonWord(line.split(":")[1].split("\t")[0],
                        Integer.parseInt(line.split(":")[1].split("\t")[1]));
                this.updateCommonWord(author, title, comWord);
            }
        }
        this.calculateFrequencies();
    }

    /**
     * Updates the FreqMap instance by adding a new entry to the set. If an entry with the same author and title exists,
     * the field value is updated with the specified value parameter.
     *
     * @param author the author's name.
     * @param title  the title of the writing.
     * @param field  the field name to be updated.
     * @param value  the value to be added/overwritten.
     */
    private void update(String author, String title, String field, float value) {
        // update entry map if exists an entry with the param author and title,
        // otherwise just add the whole entry to the entry set.
        for (FreqMapEntry entry : entries) {
            if (entry.getTitle().equals(title) && entry.getAuthor().equals(author)) {
                entry.getFrequencies().put(field, value);
                return;
            }
        }
        entries.add(new FreqMapEntry(author, title, field, value));
    }

    private void updateCommonWord(String author, String title, CommonWord comWord) {
        for (FreqMapEntry e : this.entries) {
            if (e.getAuthor().equals(author) && e.getTitle().equals(title)) {
                e.addCommonWord(comWord);
            }
        }
    }

    /*
     * Map inherited methods
     * */
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
        for (FreqMapEntry entry : this.entries) {
            if (o.equals(entry.getAuthor()))
                return true;
        }

        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        for (FreqMapEntry entry : this.entries) {
            if (entry.getFrequencies().containsValue(o))
                return true;
        }

        return false;
    }

    @Override
    public HashMap<String, Float> get(Object o) {
        for (FreqMapEntry entry : entries) {
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
        if (o instanceof FreqMapEntry)
            this.entries.remove(o);

        for (FreqMapEntry entry : entries) {
            if (entry.getAuthor().equals(o)) {
                this.entries.remove(entry);
            }
        }

        return null;

    }

    @Override
    public void putAll(Map<? extends String, ? extends HashMap<String, Float>> map) {
    }

    @Override
    public void clear() {
        this.entries.clear();
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> names = new HashSet<>();
        for (FreqMapEntry entry : entries) {
            names.add(entry.getAuthor());
        }

        return names;
    }

    @Override
    public Collection<HashMap<String, Float>> values() {
        HashSet<HashMap<String, Float>> set = new HashSet<>();
        for (FreqMapEntry entry : entries) {
            set.add(entry.getFrequencies());
        }

        return set;
    }

    @Override
    public Set<Entry<String, HashMap<String, Float>>> entrySet() {
        HashSet<Entry<String, HashMap<String, Float>>> set = new HashSet<>();
        for (final FreqMapEntry entry : entries) {
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
