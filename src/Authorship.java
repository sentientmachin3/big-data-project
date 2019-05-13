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
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma", "però", "dunque", "anzi", "che"));

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Authorship(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());
        TextInputFormat.setInputPaths(job, new Path(args[1]));
        TextOutputFormat.setOutputPath(job, new Path(args[2]));
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final static IntWritable ONE = new IntWritable(1);
        private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*"); //\ string* \ blank \\ string*\\

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String line = lineText.toString();
            for (String word : WORD_BOUNDARY.split(line)) {
                if (!word.isEmpty()) {
                    context.write(new Text(word), ONE);
                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, IntWritable, Text, FloatWritable> {
        public static int TEXT_LENGTH = 0;

        @Override
        public void reduce(Text word, Iterable<IntWritable> counts, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            int conj_sum = 0;


            for (IntWritable count : counts) {
                if (Authorship.CONJUNCTIONS.contains(word.toString()))
                    conj_sum += count.get();

                sum += count.get();
                TEXT_LENGTH += sum;
            }
            context.write(word, new FloatWritable(conj_sum / TEXT_LENGTH));
        }
    }
}
