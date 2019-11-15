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
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Authorship extends Configured implements Tool {
    private static final List<String> CONJUNCTIONS = new ArrayList<>(Arrays.asList("and", "or", "not"));

    private static final List<String> ARTICLES = new ArrayList<>(Arrays.asList("the", "a", "an"));

    private static final List<String> PREPOSITIONS = new ArrayList<>(Arrays.asList("of", "to", "from", "in", "with", "on", "for", "between"));

    static final String INPUT_PATH = "/user/root/authorship/input";
    static final String OUTPUT_PATH = "/user/root/authorship/output";
    private static final String UNKNOWNS_INPUT_PATH = "/user/root/authorship/input/unknowns";


    @Override
    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(this.getConf(), "authorship");
        job.setJarByClass(this.getClass());
        TextInputFormat.setInputPaths(job, new Path(INPUT_PATH));
        TextInputFormat.setInputPaths(job, new Path(UNKNOWNS_INPUT_PATH));
        TextOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));

        // job setup
        for (String s : Main.buildPaths(this))
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
        private static final Pattern END_PERIOD = Pattern.compile("[a-z][.!?]");
        private static final Pattern MARKS_COMMAS = Pattern.compile("[,!?]");
        private static final Pattern DIALOGUE = Pattern.compile("[\u201C\u201D]");

        @Override
        public void map(LongWritable offset, Text lineText, Context context) throws IOException, InterruptedException {
            String filePathString = ((FileSplit) context.getInputSplit()).getPath().getName();
            for (String word : WORD_BOUNDARY.split(lineText.toString())) {
                if (!word.isEmpty()) {
                    if (Authorship.ARTICLES.contains(word.toLowerCase()) || word.toLowerCase().startsWith("l'") || word.toLowerCase().startsWith("un'") ||
                            word.toLowerCase().startsWith("gl'")) {
                        context.write(new Text(filePathString + "*article"), new IntWritable(1));
                    }

                    if (Authorship.CONJUNCTIONS.contains(word.toLowerCase())) {
                        context.write(new Text(filePathString + "*conjunction"), new IntWritable(1));
                    }

                    if (Authorship.PREPOSITIONS.contains(word.toLowerCase()) || word.toLowerCase().startsWith("d'") || word.toLowerCase().startsWith("D'")) {
                        context.write(new Text(filePathString + "*preposition"), new IntWritable(1));
                    }

                    context.write(new Text(filePathString + "*nwords"), new IntWritable(1));
                }
            }

            // period number count
            Matcher matcher = END_PERIOD.matcher(lineText.toString());
            while (matcher.find()) {
                context.write(new Text(filePathString + "*periods"), new IntWritable(1));
            }

            // commas number count
            Matcher commas = MARKS_COMMAS.matcher(lineText.toString());
            while (commas.find()) {
                context.write(new Text(filePathString + "*commas"), new IntWritable(1));
            }

            Matcher dialogue = DIALOGUE.matcher(lineText.toString());
            while (dialogue.find()) {
                context.write(new Text(filePathString + "*dialogue"), new IntWritable(1));
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

            context.write(key.toString().split("\\*")[0] + key.toString().split("\\*")[1], sum);

        }
    }
}


