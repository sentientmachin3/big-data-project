package main.java;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {
        // args contains names of unknown authors file

        Authorship authorship = new Authorship();
        ToolRunner.run(authorship, args);
        FileSystem fs = FileSystem.get(authorship.getConf());
        FreqMap freqMap = new FreqMap();

        freqMap.fromFile(fs, new Path(Authorship.OUTPUT_PATH + "/part-r-00000"));
        freqMap.toFile(fs, new Path(Authorship.OUTPUT_PATH + "/known-frequencies.txt"));

        FreqMap unknownFreqMap = new FreqMap();
        for (Map.Entry<String, HashMap<String, Float>> m : freqMap.entrySet()) {
            for (String s:args) {
                if (s.contains(m.getKey())) {
                    unknownFreqMap.put(m.getKey(), m.getValue());
                }
            }
        }
    }


}
