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
    private FreqMap freqMap;
    private ArrayList<AffinityMap> deltas;

    /**
     * Generates an instance, using a FreqMap of known authors.
     *
     * @param known the FreqMap instance with the known author's data.
     */
    public SimilarityAnalysis(FreqMap known) {
        this.freqMap = known;
        this.deltas = new ArrayList<>();
        this.exec();
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
    private void exec() {
        // remove non globals values and sort entries
        ArrayList<FreqMapEntry> unknowns = new ArrayList<>();
        ArrayList<FreqMapEntry> knowns = new ArrayList<>();

        for (FreqMapEntry entry : this.freqMap.getEntries()) {
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

        Collections.sort(this.deltas, new Comparator<AffinityMap>() {
            // comparison method between two affinity maps, used to sort the analysis
            @Override
            public int compare(AffinityMap affinityMap, AffinityMap t1) {
                return affinityMap.compareTo(t1);
            }
        });
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
        for (AffinityMap a : this.deltas) {
            a.setFreqMap(this.freqMap);
            sb.append(a.toString());
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

        // adding the ranking in order to have an author's rank
        af.addRanking(new Ranking(unk, knowns));

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
