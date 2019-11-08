package main.java;


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
        for (FreqMapEntry entry: this.freqMap.getEntries()) {
            if (!entry.getTitle().contains("global")) {
                this.freqMap.remove(entry);
            }
        }
    }

    private void exec() {
        this.removeNonGlobals();

        // remove non globals values and sort entries
        ArrayList<FreqMapEntry> unknowns = new ArrayList<>();
        ArrayList<FreqMapEntry> knowns = new ArrayList<>();
        for (FreqMapEntry entry: this.freqMap.getEntries()) {
            if (entry.getTitle().contains("unknown")) {
                unknowns.add(entry);
            } else {
                knowns.add(entry);
            }
        }

        // calc deltas for each FreqMapEntry combination
        for (FreqMapEntry kn: knowns) {
            for (FreqMapEntry unk: unknowns) {
                this.deltas.add(computedDelta(kn, unk));
            }
        }


    }

    private AffinityMap computedDelta(FreqMapEntry kn, FreqMapEntry unk) {
        AffinityMap af = new AffinityMap(kn.getAuthor(), unk.getAuthor());

        // delta diff = kn - unk for each field in map
        for (String field: kn.getFrequencies().keySet()) {
            af.append(field, kn.getFrequencies().get(field) - unk.getFrequencies().get(field));
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
