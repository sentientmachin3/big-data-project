package main.java;

import org.apache.hadoop.metrics2.sink.ganglia.AbstractGangliaSink;

import java.lang.reflect.Array;
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
        ArrayList<AffinityMap> list = new ArrayList<>();

        for (String kn: known.keySet()) {
            for (String unk: unknown.keySet()) {
                AffinityMap am = new AffinityMap(kn, unk);
                for (String field: known.get(kn).keySet()) {
                    am.append(field, known.get(kn).get(field) - unknown.get(unk).get(field));
                }
                list.add(am);
            }
        }

        return list;
    }
}
