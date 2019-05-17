import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WordValue implements Writable {
    private IntWritable length;
    private static final IntWritable one = new IntWritable(1);

    public WordValue() {
        // default constructor used for reflection (hadoop)
        this.length = new IntWritable(0);
    }

    public WordValue(String word) {
        this.length = new IntWritable(word.length());
    }

    public IntWritable getLength() {
        return length;
    }

    public IntWritable getOne() {
        return one;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        length.write(dataOutput);
        one.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        length.readFields(dataInput);
        one.readFields(dataInput);
    }
}
