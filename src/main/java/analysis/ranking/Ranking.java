package main.java.analysis.ranking;

import main.java.analysis.frequencies.CommonWord;
import main.java.analysis.frequencies.FreqMapEntry;

import java.util.*;

public class Ranking {
    private FreqMapEntry unknownEntry;
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

        // sorting the ranking for this unknown entry
        this.sort();
    }

    private void sort() {
        Collections.sort(this.ranking, new Comparator<Pair<FreqMapEntry, Integer>>() {
            @Override
            public int compare(Pair<FreqMapEntry, Integer> o1, Pair<FreqMapEntry, Integer> o2) {
                return o1.getSecond().compareTo(o2.getSecond());
            }
        });
    }

    public ArrayList<String> getSortedRanking() {
        ArrayList<String> sortedRanking = new ArrayList<>();
        for (Pair<FreqMapEntry, Integer> p: this.ranking) {
            sortedRanking.add(p.getFirst().getAuthor());
        }

        return sortedRanking;
    }


}
