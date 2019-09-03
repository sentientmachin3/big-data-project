package main.java;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Authorship extends Configured implements Tool {
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("e", "né", "o", "inoltre", "ma", "però", "dunque", "anzi", "che"));
    private static final List<String> ARTICLES = new ArrayList<>(Arrays.asList("il", "lo", "la", "i", "gli", "le", "l'", "un", "una", "uno", "un'"));

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Authorship(), args);
        System.exit(0);
    }

    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());

        // job setup
//        TextInputFormat.setInputPaths(job, org.java.authorship.IniParser.getInputPath());
        TextOutputFormat.setOutputPath(job, IniParser.getOutputPath());
        for (String s : IniParser.getAuthorsPaths())
            FileInputFormat.addInputPath(new Job(), new Path(s));

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
            for (String word : WORD_BOUNDARY.split(lineText.toString())) {
                if (!word.isEmpty()) {
                    if (Authorship.ARTICLES.contains(word)) {
                        context.write(new Text("article"), new IntWritable(1));
                    }

                    if (Authorship.CONJUNCTIONS.contains(word)) {
                        context.write(new Text("conjunction"), new IntWritable(1));
                    }

                    context.write(new Text("nwords"), new IntWritable(1));
                }
            }
        }
    }


    public static class Reduce extends Reducer<Text, IntWritable, String, Integer> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable count : values) {
                sum += count.get();
            }

            context.write(key.toString(), sum);

        }
    }
}

