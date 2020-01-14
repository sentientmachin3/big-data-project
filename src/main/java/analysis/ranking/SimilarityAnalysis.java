package main.java.analysis.ranking;


import main.java.analysis.frequencies.FreqMap;
import main.java.analysis.frequencies.FreqMapEntry;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The class contains the methods in order to run a similarity analysis between authors.
 */
public class SimilarityAnalysis {
    public static SimilarityAnalysis INSTANCE = new SimilarityAnalysis();
    private ArrayList<AffinityMap> deltas = new ArrayList<>();
    private ArrayList<Ranking> rankings = new ArrayList<>();

    /**
     * Generates an instance.
     */
    private SimilarityAnalysis() {
    }

    public ArrayList<AffinityMap> getDeltas() {
        return this.deltas;
    }

    /**
     * Runs the analysis. For each couple of known/unknown author generates an AffinityMap instance.
     * The analysis runs in two phases: <ul>
     * <li> Calculates the deltas for each couple of authors;</li>
     * <li> Sorts the AffinityMap instances in order to have the most similar and the less similar in order.</li>
     * </ul>
     */
    public void exec() {
        // remove non globals values and sort entries
        ArrayList<FreqMapEntry> unknowns = new ArrayList<>();
        ArrayList<FreqMapEntry> knowns = new ArrayList<>();

        for (FreqMapEntry entry : FreqMap.INSTANCE) {
            if (entry.isUnknown() && entry.isGlobal()) {
                unknowns.add(entry);
            } else if (!entry.isUnknown() && entry.isGlobal()) {
                knowns.add(entry);
            }
        }

        // calc deltas for each FreqMapEntry combination
        for (FreqMapEntry unk : unknowns) {
            for (FreqMapEntry kn : knowns) {
                this.deltas.add(computeDelta(kn, unk, knowns));
            }
        }

        for (FreqMapEntry entry : FreqMap.INSTANCE.getUnknownEntries()) {
            this.rankings.add(new Ranking(entry, FreqMap.INSTANCE.getKnownEntries()));
        }


    }

    /**
     * Writes the current SimilarityAnalysis to an output file.
     *
     * @param fs         the filesystem where the file is written.
     * @param outputPath the path where the file is about to be saved.
     * @throws IOException if an IOException writing the file occurs.
     */
    public void toFile(FileSystem fs, Path outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Ranking r : this.rankings) {
            sb.append(r.toString());
        }

        FSDataOutputStream outputStream = fs.create(outputPath);
        outputStream.writeBytes(sb.toString());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Calculates the deltas between the frequency map of a known and an unknown author.
     * The operation performed is the absolute value of the difference between respective field names of the two maps.
     *
     * @param kn  the known author frequency map.
     * @param unk the unknown frequency map.
     * @return an AffinityMap instance containing the comparison result.
     */
    private AffinityMap computeDelta(FreqMapEntry kn, FreqMapEntry unk, ArrayList<FreqMapEntry> knowns) {
        AffinityMap af = new AffinityMap(kn.getAuthor(), unk.getAuthor());

        // delta diff = |kn - unk| for each field in map
        for (String field : kn.getFrequencies().keySet()) {
            if (!field.equals("nwords") && !field.equals("periods"))
                af.append(field, Math.abs(kn.getFrequencies().get(field) - unk.getFrequencies().get(field)));
        }

        return af;
    }

    public AffinityMap getByAuthors(String knownAuthor, String unknownAuthor) {
        for (AffinityMap affinityMap : this.deltas) {
            if (affinityMap.getAuthor().equals(knownAuthor) && affinityMap.getUnknown().equals(unknownAuthor)) {
                return affinityMap;
            }
        }

        return null;
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
