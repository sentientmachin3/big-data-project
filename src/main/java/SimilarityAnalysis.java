package main.java;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;

public class SimilarityAnalysis {
    private FreqMap freqMap;
    private ArrayList<AffinityMap> deltas;

    public SimilarityAnalysis(FreqMap known) {
        this.freqMap = known;
        this.deltas = new ArrayList<>();
        this.exec();
    }

    private void removeNonGlobals() {
        for (FreqMapEntry entry : this.freqMap.getEntries()) {
            if (!entry.getTitle().contains("global")) {
                this.freqMap.removeByEntry(entry);
            }
        }
    }

    private void exec() {
        this.removeNonGlobals();

        // remove non globals values and sort entries
        ArrayList<FreqMapEntry> unknowns = new ArrayList<>();
        ArrayList<FreqMapEntry> knowns = new ArrayList<>();
        for (FreqMapEntry entry : this.freqMap.getEntries()) {
            if (entry.getTitle().contains("unknown") && entry.getAuthor().contains("unknown")) {
                unknowns.add(entry);
//                System.out.println("u   " + entry);
            } else {
                knowns.add(entry);
//                System.out.println("k   " + entry);
            }
        }

        // calc deltas for each FreqMapEntry combination
        for (FreqMapEntry kn : knowns) {
            for (FreqMapEntry unk : unknowns) {
                this.deltas.add(computedDelta(kn, unk));
            }
        }

//        this.sort();
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

//    private void sort() {
//
//    }

    private AffinityMap computedDelta(FreqMapEntry kn, FreqMapEntry unk) {
        AffinityMap af = new AffinityMap(kn.getAuthor(), unk.getAuthor());

        // delta diff = kn - unk for each field in map
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
