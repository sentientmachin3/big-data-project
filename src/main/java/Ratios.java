package main.java;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Ratios extends Configured implements Tool {
    public int run(String[] args) throws Exception {
        // resources setup
        FileSystem fs = FileSystem.get(this.getConf());
        // fixme sistema nomi autori
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(fs.open(new Path(Authorship.INPUT_PATH))));
        OutputStream os = fs.create(new Path(Authorship.OUTPUT_PATH + "/dante-ratios.txt"));
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

        RatiosMap ratiosMap = new RatiosMap(inputReader);
        for (String s : ratiosMap.getMap().keySet()) {
            br.write(s + " = " + ratiosMap.getMap().get(s) + "\n");
        }

        br.flush();
        br.close();
        os.close();

        return 0;
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new Ratios(), args));
    }

}
