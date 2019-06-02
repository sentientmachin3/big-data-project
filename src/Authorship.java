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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class Authorship extends Configured implements Tool {
    private static final IntWritable ONE = new IntWritable(1);
    private static final String INPUT_PATH = "/user/davide/authorship/input";
    private static final String OUTPUT_PATH = "/user/davide/authorship/output";
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma", "però", "dunque", "anzi", "che"));
    private static final Globals GLOBALS = new Globals();

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Authorship(), args);
        System.exit(res);
    }

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
        private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*"); //\ string* \ blank \\ string*\\

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String line = lineText.toString();
            for (String word : WORD_BOUNDARY.split(line)) {
                GLOBALS.incrementTextLength();
                if (!word.isEmpty() && Authorship.CONJUNCTIONS.contains(word)) {
                    context.write(new Text(word), ONE);
                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, IntWritable, Text, FloatWritable> {

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int conjSum = 0;
            for (IntWritable value : values) {
                if (Authorship.CONJUNCTIONS.contains(key.toString())) {
                    conjSum += value.get();
                }
            }
            context.write(key, new FloatWritable(conjSum / GLOBALS.getTextLength()));
        }
    }
}
