package org.commoncrawl.hadoop.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
		return inputs == null ? null : EscapeUtils.split(',', inputs);
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

		logger.info("Sanitizing '"+dirty_uri+"'");

		if( dirty_uri.matches("^/user") ){ // matches /user/foo/bar addresses
			logger.info("Returning '"+def_fs + dirty_uri +"' as sanitized uri");
			return def_fs + dirty_uri;
		}
		else if( dirty_uri.matches("^"+def_fs) ){
			logger.info("Returning '"+dirty_uri +"' as sanitized uri");
			return dirty_uri;
		}
		else if( dirty_uri.matches("^hdfs://") ){
			logger.info("Returning '"+ dirty_uri.replaceFirst("^hdfs://", def_fs) +"' as sanitized uri");
			return dirty_uri.replaceFirst("^hdfs://", def_fs);
		}else
			return def_fs + dirty_uri;

	}

	@Override
	protected Collection<ARCResource> getARCResources(JobConf job)
			throws IOException {
	
		String[] resources = getInputs(job);

		Map<String, ARCResource> arc_resources = new HashMap<String, ARCResource>();

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

			arc_resources.put(arc_file.toString() , new ARCResource(arc_file.toUri().toASCIIString(), filest.getLen() ));


		}

		return arc_resources.values();

	}






}
