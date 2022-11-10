package bigdata.hadoop.mapreduce.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Iterator;

public class ReduceClass extends org.apache.hadoop.mapreduce.Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context)
			throws IOException, InterruptedException {
	
		int sum = 0;
		Iterator<IntWritable> valuesIt = values.iterator();
		
		while(valuesIt.hasNext()){
			sum += valuesIt.next().get();
		}
		
		context.write(key, new IntWritable(sum));
	}	
}