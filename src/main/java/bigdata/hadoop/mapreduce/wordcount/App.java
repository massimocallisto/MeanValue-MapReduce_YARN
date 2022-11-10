package bigdata.hadoop.mapreduce.wordcount;

import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
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
		//Job job = new Job();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(App.class);
		//job.setJobName("Word count example");
		
		// Read all files contained in the directory
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setOutputFormatClass(TextOutputFormat.class); // line by line, key TAB value\n write
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(ReduceClass.class);

		// Block or not
		int returnValue = job.waitForCompletion(true) ? 0:1;
		
		if(job.isSuccessful()) {
			System.out.println("Job was successful");
		} else if(!job.isSuccessful()) {
			System.out.println("Job was not successful");			
		}
		
		return returnValue;
	}
}
