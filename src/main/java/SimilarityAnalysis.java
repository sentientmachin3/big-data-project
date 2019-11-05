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

    private void exec() {
        for (String kn : known.keySet()) {
            for (String unk : unknown.keySet()) {
                AffinityMap am = new AffinityMap(kn, unk);
                for (String field : known.get(kn).keySet()) {
                    am.append(field, known.get(kn).get(field) - unknown.get(unk).get(field));
                }
                affinityMaps.add(am);
            }
        }
    }
}
