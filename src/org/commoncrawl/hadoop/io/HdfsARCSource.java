package org.commoncrawl.hadoop.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.log4j.Logger;
import org.commoncrawl.util.shared.EscapeUtils;

public class HdfsARCSource extends ARCSplitCalculator implements ARCSource, JobConfigurable{

	private static final Logger logger = Logger.getLogger(HdfsARCSource.class);

	private JobConf jc = null;

	/**
	 * <tt>hdfs.arc.source.inputs</tt> - the property where the list of inputs is
	 * stored in hdfs.
	 * 
	 * @see #setInputs
	 * @see #getInputs
	 */
	public static final String P_INPUTS = "hdfs.arc.source.inputs";

	/**
	 * Sets the list of inputs that will be processed.
	 * 
	 * <p>
	 * Paths to add should either be for gzipped ARC files, or directories
	 * containing gzipped ARC files.
	 * 
	 * @param job
	 *          the job to set the inputs for
	 * @param paths
	 *          the paths to set as inputs
	 * 
	 * @see #P_INPUTS
	 */
	public static void setInputs(JobConf job, String... paths) {
		job.set(P_INPUTS, EscapeUtils.concatenate(',', paths));
	}

	/**
	 * Returns the list of inputs set by {@link setInputs}.
	 * 
	 * @param job
	 *          the job to get the inputs from
	 * 
	 * @return the list of inputs, or <tt>null</tt> if not set
	 */
	public static String[] getInputs(JobConf job) {
		String inputs = job.get(P_INPUTS);

		if(inputs == null){
			logger.warn("P_INPUT variable is not set, trying with FileInputFormat.getInputPaths()");
		}else
			return EscapeUtils.split(',', inputs);

		Path[] paths = FileInputFormat.getInputPaths(job);

		if(paths != null && paths.length > 0){

			String[] string_paths =  new String[paths.length];
			int index = 0;

			for(Path p : paths){
				string_paths[index++] = p.toString();
			}
			return string_paths;
		}
		else
			return null;

	}

	@Override
	protected void configureImpl(JobConf job) {
		/**
		 * Saving the job configuration so that it can be recovered when invoking getStream
		 */
		jc = job;

	}

	@Override
	public InputStream getStream(String resource, long streamPosition,
			Throwable lastError, int previousFailures) throws Throwable {

		if (lastError != null || previousFailures > 0) {
			// Don't retry...local IO failures are not expected
			return null;
		}

		if (streamPosition != 0) {
			// This shouldn't happen, but we'll check just in case
			throw new RuntimeException("Non-zero position requested");
		}

		if (jc == null)
			throw new NullPointerException("Jc is null");

		FileSystem fs = FileSystem.get(jc);

		logger.debug("getStream:: Opening: "+resource);

		FSDataInputStream is = fs.open(new Path(resource) );

		is.seek(streamPosition);

		return is;

	}

	private String sanitizeURI(String dirty_uri, JobConf job){

		String def_fs = job.get("fs.default.name");
		
		if( def_fs.endsWith("/") ) //lets delete the ending /
			def_fs = def_fs.substring(0, def_fs.length() -1);

		logger.info("Sanitizing '"+dirty_uri+"'");

		if(dirty_uri.startsWith("/user/") )
			return def_fs + dirty_uri;
		else if(dirty_uri.startsWith(def_fs) || dirty_uri.startsWith(def_fs.substring(0, def_fs.indexOf(":", 6)))) //sometimes the port number is not specified
			return dirty_uri;
		else{
			logger.warn("The file path '"+dirty_uri+"' is not valid. Please provide a full path, ie /user/<username>/<path-to file>");
			return null;
		}
	}

	@Override
	protected Collection<ARCResource> getARCResources(JobConf job)
			throws IOException {

		String[] resources = getInputs(job);

		LinkedList<ARCResource> arc_resources = new LinkedList<ARCResource>();
		
		LinkedList<FileStatus> directories = new LinkedList<FileStatus>();

		FileSystem fs = FileSystem.get(job);

		for(String current_resource : resources){

			Path arc_file = new Path(sanitizeURI(current_resource, job) );

			FileStatus filest = null;

			try{
				filest = fs.getFileStatus(arc_file);
			}
			catch(FileNotFoundException fnf){
				logger.error("Unable to open "+arc_file);
				continue;
			}
			
			/**
			 * If I find a directory I process its content to find regular files
			 */
			if (filest.isDir() ){
				
				FileStatus dirs[] = fs.listStatus(arc_file);
				
				for(FileStatus fst: dirs){
					directories.add(fst);
				}
				continue;
			}
			else
				arc_resources.add( new ARCResource(arc_file.toUri().toASCIIString(), filest.getLen() ));


		}
		
		/**
		 * Processing directories, if any
		 */
		
		while( !directories.isEmpty() ){
			
			FileStatus current_fs = directories.pop();
			
			if (current_fs.isDir() ){

				FileStatus dirs[] = fs.listStatus(current_fs.getPath());

				for(FileStatus fst: dirs){
					directories.add(fst);
				}
				continue;
			}
			else
				arc_resources.add( new ARCResource(current_fs.getPath().toUri().toASCIIString(), current_fs.getLen() ));
		}

		return arc_resources;

	}






}
