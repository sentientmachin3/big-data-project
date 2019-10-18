package main.java;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Authorship authorship = new Authorship();
        FreqMap frequencies = new FreqMap();

        ToolRunner.run(authorship, args);
        FileSystem fs = FileSystem.get(authorship.getConf());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(Authorship.INPUT_PATH + "/part-r-00000")));

        while (bufferedReader.readLine() != null) {
            String line = bufferedReader.readLine();
            String author = line.split(".txt")[0];
            String field = line.split(".txt")[1].split(" = ")[0];
            int value = Integer.parseInt(line.split(".txt")[1].split(" = ")[1]);

            if (field.equals("nwords")) {
                value /= Integer.parseInt(field);
            }

            frequencies.setValue(author, field, value);
        }





    }

}
