package main.java.analysis.frequencies;

import main.java.hadoop.Authorship;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Class representing a FreqMapEntry instance. A FreqMap is a set of entries (instances of FreqMapEntry) containing,
 * for each author and each text, the results of the analysis of a single input file.
 */
public class FreqMap implements Iterable<FreqMapEntry> {
    public static FreqMap INSTANCE = new FreqMap();
    private HashSet<FreqMapEntry> entries = new HashSet<>();

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
        // for each entry computes the frequency of articles, conjunctions and prepositions by dividing the counted words
        // by the total number of words

        DecimalFormat df = new DecimalFormat("#.###");
        for (FreqMapEntry entry : entries) {
            for (String field : entry.getFrequencies().keySet()) {
                if (field.equals("articles") || field.equals("conjunctions") || field.equals("prepositions") ||
                        field.equals("commas") || field.equals("pronouns") || field.equals("verbs")) {
                    float upval = entry.getFrequencies().get(field) / entry.getFrequencies().get("nwords");
                    entry.getFrequencies().put(field, Float.valueOf(df.format(upval)));
                }
            }

            // calculates the most common words frequency on the total number of words
            for (CommonWord w : entry.getHighestFrequencyList()) {
                w.setValue(w.getValue() / entry.getFrequencies().get("nwords"));
            }

            // computes the average period length by dividing the total number of words by the number of periods.
            entry.getFrequencies().put("avg_period_length", entry.getFrequencies().get("nwords") /
                    entry.getFrequencies().get("periods"));

            // since the unknown authors have a single entry, it's useless to generate another global entry for them
            if (entry.isUnknown()) {
                entry.setTitle("global");
                entry.buildTopTen();
            }
        }

        // call the method for global frequencies (average of author's parameters)
        ArrayList<FreqMapEntry> globals = new ArrayList<>();
        for (FreqMapEntry entry : this) {
            if (!entry.getAuthor().contains("unknown"))
                globals.add(globalAuthorFrequency(entry.getAuthor()));
        }

        this.entries.addAll(globals);
        this.removeNonGlobals();

    }

    private void removeNonGlobals() {
        for (FreqMapEntry entry: this.entries) {
            if (!entry.isGlobal() && !entry.isUnknown() ) {
                this.entries.remove(entry);
            }
        }
    }

    public ArrayList<FreqMapEntry> getUnknownEntries() {
        ArrayList<FreqMapEntry> unknownEntries = new ArrayList<>();

        for (FreqMapEntry e : this.entries) {
            if (e.getAuthor().contains("unknown") && e.getTitle().contains("global")) {
                unknownEntries.add(e);
            }
        }

        return unknownEntries;
    }

    public ArrayList<FreqMapEntry> getKnownEntries() {
        ArrayList<FreqMapEntry> knownEntries = new ArrayList<>();

        for (FreqMapEntry e : this.entries) {
            if (!e.getAuthor().contains("unknown") && e.getTitle().contains("global")) {
                knownEntries.add(e);
            }
        }

        return knownEntries;
    }


    /**
     * Generates a global frequency for each author by computing the average values for each field in the frequency map.
     * Substitutes all the entries of an author with a single entry with the same author name and "global" as title.
     *
     * @param author the author's name to compute the average of.
     */
    private FreqMapEntry globalAuthorFrequency(String author) {

        FreqMapEntry global = new FreqMapEntry(author, "global");

        // init map to 0
        for (String field : entries.iterator().next().getFrequencies().keySet()) {
            global.getFrequencies().put(field, (float) 0);
        }

        ArrayList<FreqMapEntry> authorEntries = new ArrayList<>();
        HashSet<CommonWord> wordsPerAuthor = new HashSet<>();
        HashMap<String, Float> globalWordsPerAuthor = new HashMap<>();
        ArrayList<CommonWord> res = new ArrayList<>();

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
        for (FreqMapEntry a : authorEntries) {
            for (String s : global.getFrequencies().keySet()) {
                global.getFrequencies().put(s, global.getFrequencies().get(s) + a.getFrequencies().get(s));

            }
        }

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

        global.setHighestFrequencyList(res);
        global.buildTopTen();
        return global;
    }

    public void toFile(FileSystem fs, Path path) throws IOException {
        FSDataOutputStream outputStream = fs.create(path);
        for (FreqMapEntry entry : this.entries) {
            if (entry.isGlobal()) {
                outputStream.writeBytes(entry.toString());
                outputStream.flush();
            }
        }

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
    public FreqMap load(FileSystem fs, Path path) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));

        // value parsing
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String author = line.split("-")[0];
            String title = line.split(".txt\\*")[0].substring(line.split(".txt\\*")[0].indexOf("-") + 1);
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
        return this;
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

    /**
     * Adds a new instance of common word to the FreqMapEntry the author and title refer.
     *
     * @param author  the author's name
     * @param title   the title of the writing
     * @param comWord the common word to be added
     */
    private void updateCommonWord(String author, String title, CommonWord comWord) {
        for (FreqMapEntry e : this.entries) {
            if (e.getAuthor().equals(author) && e.getTitle().equals(title)) {
                e.addCommonWord(comWord);
            }
        }
    }

    /**
     * Collects the most common words for an author.
     *
     * @param author the author's name
     * @return the common words as an ArrayList instance
     */
    public ArrayList<CommonWord> getCWSFromAuthor(String author) {
        for (FreqMapEntry entry : this.entries) {
            if (entry.getAuthor().equals(author) && entry.isGlobal()) {
                return entry.getHighestFrequencyList();
            }
        }

        return null;
    }


    @Override
    public Iterator<FreqMapEntry> iterator() {
        return this.entries.iterator();
    }

}
