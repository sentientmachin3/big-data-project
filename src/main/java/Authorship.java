package main.java;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.ini4j.Ini;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Authorship extends Configured implements Tool {
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma",
            "però", "dunque", "anzi", "che", "and", "or", "not"));
    private static final List<String> ARTICLES = new ArrayList<>(Arrays.asList("the", "a", "an", "il", "lo", "la", "i",
            "gli", "le", "l'", "un", "una", "uno", "un'"));
    private static List<String> AUTHORS = new LinkedList<>();
    public static final String INPUT_PATH = "/user/root/authroship/input";
    public static final String OUTPUT_PATH = "/user/root/authroship/output";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Authorship(), args);
        System.exit(0);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());

        // todo: fix output format
        // job setup
//        TextInputFormat.setInputPaths(job, org.java.authorship.IniParser.getInputPath());
//        TextOutputFormat.setOutputPath(job, IniParser.getOutputPath());
        for (String s : AUTHORS)
            FileInputFormat.addInputPath(job, new Path(s));

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(String.class);
        job.setOutputValueClass(Integer.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
        private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s *");

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String filePathString = ((FileSplit) context.getInputSplit()).getPath().getName();
            for (String word : WORD_BOUNDARY.split(lineText.toString())) {
                if (!word.isEmpty()) {
                    if (Authorship.ARTICLES.contains(word)) {
                        context.write(new Text(filePathString + "*article"), new IntWritable(1));
                    }

                    if (Authorship.CONJUNCTIONS.contains(word)) {
                        context.write(new Text(filePathString + "*conjunction"), new IntWritable(1));
                    }

                    context.write(new Text(filePathString + "*nwords"), new IntWritable(1));
                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, IntWritable, String, Integer> {
        private MultipleOutputs<Text, IntWritable> out;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            out = new MultipleOutputs<Text, IntWritable>((TaskInputOutputContext) context);
        }

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable count : values) {
                sum += count.get();
            }


            out.write(key.toString().split("\\*")[0], key.toString().split("\\*")[1], new IntWritable(sum));

        }
    }
}


