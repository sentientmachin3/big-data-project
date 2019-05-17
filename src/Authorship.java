import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class Authorship extends Configured implements Tool {
    private static long TEXT_LENGTH = 0;
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma", "però", "dunque", "anzi", "che"));

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Authorship(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());
        TextInputFormat.setInputPaths(job, new Path("/user/davide/authorship/input"));
        TextOutputFormat.setOutputPath(job, new Path("/user/davide/authorship/output"));
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(WordValue.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, WordValue> {
        private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*"); //\ string* \ blank \\ string*\\

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String line = lineText.toString();
            for (String word : WORD_BOUNDARY.split(line)) {
                if (!word.isEmpty()) {
                    context.write(new Text(word), new WordValue(word));
                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, WordValue, Text, FloatWritable> {
        @Override
        protected void reduce(Text key, Iterable<WordValue> values, Context context) throws IOException, InterruptedException {
            for (WordValue w : values) {
                int conjSum = 0;
                Authorship.TEXT_LENGTH += w.getLength().get();
                if (Authorship.CONJUNCTIONS.contains(key.toString())) {
                    conjSum += w.getOne().get();
                    context.write(key, new FloatWritable(conjSum / Authorship.TEXT_LENGTH));
                }
            }
        }
    }
}
