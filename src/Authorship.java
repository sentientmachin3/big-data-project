import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.util.hash.Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class Authorship extends Configured implements Tool {
    private static final String INPUT_PATH = "/user/davide/authorship/input";
    private static final String OUTPUT_PATH = "/user/davide/authorship/output";
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma", "però", "dunque", "anzi", "che"));
    private static final List<String> ARTICLES = new ArrayList<>(Arrays.asList("il", "lo", "la", "i", "gli", "le"));
    private static final List<String> SPEECH_PARTS = new ArrayList<>(Arrays.asList("conjunction", "article", "name", "verb", "pronoun"));

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Authorship(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());

        // job setup
        TextInputFormat.setInputPaths(job, new Path(INPUT_PATH));
        TextOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        Pattern PERIOD = Pattern.compile(".*[.!?]");

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            for (String word : PERIOD.split(lineText.toString())) {
                if (!word.isEmpty()) {
                    if (Authorship.CONJUNCTIONS.contains(word)) {
                        context.write(new Text("conjunction"), new IntWritable(1));
                    }

                    if (Authorship.ARTICLES.contains(word)) {
                        context.write(new Text("article"), new IntWritable(1));
                    }

                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, IntWritable, String, Integer> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            HashMap<String, IntWritable> map = new HashMap<String, IntWritable>();
            for (String part : SPEECH_PARTS) {
                map.put(part, new IntWritable(0));
            }

            for (String part : SPEECH_PARTS) {
                if (key.toString().equals(part)) {
                    map.put(part, new IntWritable(map.get(part).get() + 1));
                }
            }

            for (String k : map.keySet()) {
                context.write(k, map.get(k).get());
            }

        }
    }
}
