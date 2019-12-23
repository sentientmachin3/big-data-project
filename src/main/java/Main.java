package main.java;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The Main program. This class is responsible for running the hadoop job,
 * generating the frequency maps from the hadoop job output and running the similarity analysis.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Authorship authorship = new Authorship();
        ToolRunner.run(authorship, args);
        FileSystem fs = FileSystem.get(authorship.getConf());
        FreqMap freqMap = new FreqMap();

        freqMap.fromFile(fs, new Path(Authorship.OUTPUT_PATH + "/part-r-00000"));
//        freqMap.toFile(fs, new Path(Authorship.OUTPUT_PATH + "/known-frequencies.txt"));

        SimilarityAnalysis similarityAnalysis = new SimilarityAnalysis(freqMap);
        similarityAnalysis.toFile(fs, new Path(Authorship.OUTPUT_PATH + "/deltas.txt"));

    }

    /**
     * Builds the paths leading to the input files.
     *
     * @param authorship the hadoop job instance.
     * @return a list of paths as String instances.
     * @throws IOException if an IOException occurs (permission problems and so on...)
     */
    static LinkedList<String> buildPaths(Authorship authorship) throws IOException {
        FileSystem fs = FileSystem.get(authorship.getConf());
        RemoteIterator<LocatedFileStatus> remoteIterator = fs.listFiles(new Path(Authorship.INPUT_PATH), false);
        LinkedList<String> names = new LinkedList<>();
        while (remoteIterator.hasNext()) {
            names.add(remoteIterator.next().getPath().toString());
        }

        return names;
    }


}
