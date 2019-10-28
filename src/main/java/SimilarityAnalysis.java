package main.java;

import java.util.ArrayList;

public class SimilarityAnalysis {
    private FreqMap known;
    private FreqMap unknown;
    private ArrayList<AffinityMap> affinityMaps;

    public SimilarityAnalysis(FreqMap known, FreqMap unknown) {
        this.known = known;
        this.unknown = unknown;
        this.affinityMaps = new ArrayList<>();
        this.exec();
    }

    private ArrayList<AffinityMap> exec() {
        for (String kn: known.keySet()) {
            for (String unk: unknown.keySet()) {
                // TODO
            }
        }

        return null;
    }
}
