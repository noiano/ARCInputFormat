package org.commoncrawl.example;

import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.commoncrawl.hadoop.io.ARCInputFormat;
import org.commoncrawl.hadoop.io.HdfsARCSource;
import org.commoncrawl.protocol.shared.ArcFileItem;

public class HDFSExample extends Configured implements Tool {

  public static void main(String args[]) throws Exception {
    ToolRunner.run(new HDFSExample(), args);
  }
    
  public int run(String[] args) throws Exception {
        
    if (args.length!=1) {
      throw new RuntimeException("usage: "+getClass().getName()+" <input>");
    }
    
    JobConf conf = new JobConf(getConf(), getClass());
    conf.setJobName(getClass().getName());
    
    conf.setOutputKeyClass(NullWritable.class);
    conf.setOutputValueClass(NullWritable.class);
    conf.set("mapred.output.compress", "true");
    conf.set("mapred.output.compression.type", "BLOCK");
    conf.set("mapred.output.compression.codec", "org.apache.hadoop.io.compress.SnappyCodec");
    
    conf.setMaxMapTaskFailuresPercent(100);
    conf.setNumReduceTasks(0);
    
    conf.setInputFormat(ARCInputFormat.class);
    ARCInputFormat.setARCSourceClass(conf, HdfsARCSource.class);
    
    /**
     * Addding input file to HdfsARCsource. This step is mandatory
     */
    conf.set(HdfsARCSource.P_INPUTS, args[0] );
    
    conf.setMapperClass(MimeCounterMapper.class);    
    
    conf.setOutputFormat(NullOutputFormat.class);
    JobClient.runJob(conf);

    return 0;
  }
  
  public static class MimeCounterMapper extends MapReduceBase implements Mapper<Text,ArcFileItem,NullWritable,NullWritable> {

   
    public void map(Text url, ArcFileItem v, OutputCollector<NullWritable, NullWritable> collector, Reporter reporter) throws IOException {
        
    	/**
    	 * Let's get the mime type for the current crawled document
    	 */
        String mime_type = v.getMimeType();
        
        reporter.getCounter("FilterTextHtml.mime_types", mime_type).increment(1);        
        
        return;
        
    }   
    

    }

  
  
}