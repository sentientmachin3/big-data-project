package main.java;


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

    /**
     * Runs the analysis. For each couple of known/unknwown author generates an AffinityMap instance.
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
        for (FreqMapEntry kn : knowns) {
            for (FreqMapEntry unk : unknowns) {
                AffinityMap temp = computedDelta(kn, unk);
                kn.getHighestFrequencyList().retainAll(unk.getHighestFrequencyList());
                temp.setMatchingCommonWords(kn.getHighestFrequencyList().size());
                this.deltas.add(temp);
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
            sb.append(a.toString()).append("\n");
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
    private AffinityMap computedDelta(FreqMapEntry kn, FreqMapEntry unk) {
        AffinityMap af = new AffinityMap(kn.getAuthor(), unk.getAuthor());

        // delta diff = |kn - unk| for each field in map
        for (String field : kn.getFrequencies().keySet()) {
            if (!field.equals("nwords") && !field.equals("periods"))
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
