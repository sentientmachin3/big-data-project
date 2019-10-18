package main.java;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Authorship authorship = new Authorship();

        ToolRunner.run(authorship, args);
        FileSystem fs = FileSystem.get(authorship.getConf());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(new Path(Authorship.OUTPUT_PATH + "/part-r-00000"))));
        FreqMap frequencies = new FreqMap();

        // value parsing
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String author = line.split(".txt")[0];
            String field = line.split(".txt")[1].split("\t")[0];
            float value = Float.parseFloat(line.split(".txt")[1].split("\t")[1]);

            frequencies.setValue(author, field, value);

        }

        frequencies.calculateFrequencies();

        FSDataOutputStream outputStream = fs.create(new Path(Authorship.OUTPUT_PATH + "/frequencies.txt"));
        outputStream.writeBytes(frequencies.toString());


    }

}
