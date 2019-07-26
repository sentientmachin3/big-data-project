import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.*;

public class Ratios extends Configured implements Tool {
    private static final String FS_NAME = "fs.defaultFS";

    public int run(String[] args) throws Exception {
        System.out.println(this.getConf().get(FS_NAME));
        FileSystem fs = FileSystem.get(this.getConf());
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(fs.open(IniParser.getInputPath())));
        FileWriter outputWriter = new FileWriter(IniParser.getOutputPath().toString() + "/dante-ratios");

        RatiosMap ratiosMap = new RatiosMap(inputReader);
        for (String s:ratiosMap.getMap().keySet()) {
            outputWriter.write(s + " = " + ratiosMap.getMap().get(s));
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int returnCode = ToolRunner.run(new Ratios(), args);
        System.exit(returnCode);
    }

}
