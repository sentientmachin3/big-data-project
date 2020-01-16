package main.java.analysis.ranking;

import main.java.analysis.frequencies.CommonWord;
import main.java.analysis.frequencies.FreqMap;
import main.java.analysis.frequencies.FreqMapEntry;

import java.util.*;

public class Ranking {
    public FreqMapEntry unknownEntry;
    private ArrayList<Pair<FreqMapEntry, Integer>> ranking = new ArrayList<>();

    public Ranking(FreqMapEntry unknownEntry, ArrayList<FreqMapEntry> ranking) {
        this.unknownEntry = unknownEntry;

        // compute common words with unknown author
        int commons = 0;
        for (FreqMapEntry entry : ranking) {
            for (CommonWord c : entry.getHighestFrequencyList()) {
                if (unknownEntry.getHighestFrequencyList().contains(c)) {
                    commons++;
                }
            }
            this.ranking.add(new Pair<>(entry, commons));
            commons = 0;
        }

        // sort the ranking for this unknown entry
        this.sort();
    }

    private void sort() {
        Collections.sort(this.ranking, new Comparator<Pair<FreqMapEntry, Integer>>() {
            @Override
            public int compare(Pair<FreqMapEntry, Integer> o1, Pair<FreqMapEntry, Integer> o2) {
                return -(o1.getSecond().compareTo(o2.getSecond()));
            }
        });

        for (int i = 0; i < ranking.size() - 1; i++) {
            Pair<FreqMapEntry, Integer> p1 = ranking.get(i);
            Pair<FreqMapEntry, Integer> p2 = ranking.get(i + 1);

            if (p1.getSecond().equals(p2.getSecond())) {
                AffinityMap a1 = SimilarityAnalysis.INSTANCE.getByAuthors(p1.getFirst().getAuthor(), this.unknownEntry.getAuthor());
                AffinityMap a2 = SimilarityAnalysis.INSTANCE.getByAuthors(p2.getFirst().getAuthor(), this.unknownEntry.getAuthor());

                if (a1 != null && a2 != null && (a1.compareTo(a2) < 0)) {
                    // swap the pairs (same number of common words)
                    this.ranking.set(i, p2);
                    this.ranking.set(i + 1, p1);
                }
            }
        }
    }

    public ArrayList<String> getSortedRanking() {
        ArrayList<String> sortedRanking = new ArrayList<>();
        for (Pair<FreqMapEntry, Integer> p : this.ranking) {
            sortedRanking.add(p.getFirst().getAuthor());
        }

        return sortedRanking;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.unknownEntry.getAuthor()).append(":");
        for (String auth : this.getSortedRanking()) {
            sb.append(auth).append(" - ");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("\n");
        return sb.toString();
    }
}
