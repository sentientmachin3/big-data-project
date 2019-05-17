import org.apache.hadoop.io.IntWritable;

public class WordValue {
    private int length;
    private static final IntWritable one = new IntWritable(1);

    public WordValue(String word) {
        this.length = word.length();
    }

    public IntWritable getLength() {
        return new IntWritable(length);
    }

    public IntWritable getOne() {
        return one;
    }
}
