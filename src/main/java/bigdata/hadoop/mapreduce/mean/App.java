package bigdata.hadoop.mapreduce.mean;

import bigdata.hadoop.mapreduce.wordcount.MapClass;
import bigdata.hadoop.mapreduce.wordcount.ReduceClass;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

//import org.apache.hadoop.conf.Configured;
//import org.apache.hadoop.util.Tool;


// https://hadoopi.wordpress.com/2013/06/05/hadoop-implementing-the-tool-interface-for-mapreduce-driver/
public class App {
	
	public static void main(String[] args) throws Exception{
		//int exitCode = ToolRunner.run(new App(), args);
		//System.exit(exitCode);
		new App().run(args);
	}
 
	public int run(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.printf("Usage: %s needs two arguments <input> <output> files\n",
					getClass().getSimpleName());
			return -1;
		}
	
		//Initialize the Hadoop job and set the jar as well as the name of the Job
		Configuration conf = new Configuration();
		//conf.setStrings("measure_keys", new String[]{"evt_uuid","evt_ref","evt_tz","evt_type","k_type","device_temperature","sequence_number","environmental_temperature","relative_humidity","absolute_humidity","dew_point","pressure","back_front","orientation","movement_level","regular","posture_tilt","position","battery_level","presence","adc_channel_00","adc_channel_01","illuminance"});

		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(App.class);


		//FileInputFormat.setInputPathFilter(job, CSVFilter.class);

		// configuration should contain reference to your namenode
		FileSystem fs = FileSystem.get(conf);
		// true stands for recursively deleting the folder you gave
		fs.delete(new Path(args[1]), true);


		// Read all files contained in the directory
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		job.setOutputFormatClass(TextOutputFormat.class); // line by line, key TAB value\n write
		
		job.setMapperClass(MeasureMapper.class);
		job.setReducerClass(MeanReducer.class);

		// Block or not
		int returnValue = job.waitForCompletion(true) ? 0:1;
		
		if(job.isSuccessful()) {
			System.out.println("Job was successful");
		} else if(!job.isSuccessful()) {
			System.out.println("Job was not successful");			
		}
		
		return returnValue;
	}

	public class CSVFilter extends Configured implements PathFilter {
		public CSVFilter(Configuration c){
			super(c);
		}

		public boolean accept(Path path) {
			return  path.toString().endsWith(".csv");
		}
	}

	public static class MeasureMapper
			extends Mapper<Object, Text, Text, DoubleWritable> {
		// Object->line numeber, Text->line, Text->key_out, DoubleWritable->value_out
		private final Text key_out = new Text();
		String[] measure_keys = {"evt_uuid","evt_ref","evt_tz","evt_type","k_type","device_temperature","sequence_number","environmental_temperature","relative_humidity","absolute_humidity","dew_point","pressure","back_front","orientation","movement_level","regular","posture_tilt","position","battery_level","presence","adc_channel_00","adc_channel_01","illuminance"};
		String SEPARATOR = ",";

		public void map(Object key, Text value, Context context
		) throws IOException, InterruptedException {
			/*
			key->line number
			value->line
			*/

			if(key.toString().equals("0")){
				return;
			}

			String[] parts = value.toString().split(SEPARATOR, -1);
			if(parts.length < 23) {
				System.err.println("Got line with less than expected length of 23");
				return;
			}

			// build key with sensor ID and event time tz upt to day
			//t< -> 2020-04-01T04:59:58.477+02:00
			String key_part = parts[1] + "|" + parts[2].substring(0, 10);

			// For each remaining value we extract the corresponding measure
			//Configuration conf = context.getConfiguration();
			//String[] measure_keys = conf.getStrings("measure_keys");

			for (int i = 5; i < 23; i++) {
				try {
					String val = parts[i];
					double v = Double.parseDouble(val);
					String k = key_part + "|" + measure_keys[i];
					key_out.set(k);
					context.write(key_out, new DoubleWritable(v));
				}catch (Exception e){
					//System.err.println(e.getMessage());
				}
			}
		}
	}

	public static class MeanReducer
			extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();

		public void reduce(Text key, Iterable<DoubleWritable> values,
						   Context context
		) throws IOException, InterruptedException {
			double sum = 0;
			int count = 0;
			for (DoubleWritable val : values) {
				sum += val.get();
				count++;
			}
			result.set(sum/count);
			context.write(key, result);
		}
	}
}
