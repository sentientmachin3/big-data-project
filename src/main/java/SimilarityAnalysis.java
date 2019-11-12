package main.java;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SimilarityAnalysis {
    private FreqMap freqMap;
    private ArrayList<AffinityMap> deltas;

    public SimilarityAnalysis(FreqMap known) {
        this.freqMap = known;
        this.deltas = new ArrayList<>();
        this.exec();
    }

    private void exec() {
        // remove non globals values and sort entries
        ArrayList<FreqMapEntry> unknowns = new ArrayList<>();
        ArrayList<FreqMapEntry> knowns = new ArrayList<>();
        HashSet<FreqMapEntry> temp = new HashSet<>(this.freqMap.getEntries());

        for (FreqMapEntry entry : temp) {
            if (entry.getAuthor().contains("unknown")) {
                unknowns.add(entry);
                temp.remove(entry);
            }

            if (entry.getTitle().equals("global")) {
                knowns.add(entry);
                temp.remove(entry);
            }
        }

        // calc deltas for each FreqMapEntry combination
        for (FreqMapEntry kn : knowns) {
            for (FreqMapEntry unk : unknowns) {
                this.deltas.add(computedDelta(kn, unk));
            }
        }
    }

    public void toFile(FileSystem fs, Path outputPath) throws IOException {
        StringBuilder sb = new StringBuilder("");
        for (AffinityMap a : this.deltas) {
            sb.append(a.toString()).append("\n");
        }

        FSDataOutputStream outputStream = fs.create(outputPath);
        outputStream.writeBytes(sb.toString());
        outputStream.flush();
        outputStream.close();
    }

    private AffinityMap computedDelta(FreqMapEntry kn, FreqMapEntry unk) {
        AffinityMap af = new AffinityMap(kn.getAuthor(), unk.getAuthor());

        // delta diff = |kn - unk| for each field in map
        for (String field : kn.getFrequencies().keySet()) {
            af.append(field, Math.abs(kn.getFrequencies().get(field) - unk.getFrequencies().get(field)));
        }

        return af;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
