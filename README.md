This project extracts from the [original commoncrawl project](https://github.com/commoncrawl/commoncrawl) only the ARCInputFormat class and its dependencies. It also implement a new ARCSource, HDFSSource, which allows ARC files to be read from HDFS.

## Differences from the original project:
+ JetS3tARCSource is removed since it is not advisable to read directly from S3. It's more efficient to copy (using [distcp](http://hadoop.apache.org/common/docs/current/distcp.html)) the arc files from S3 to HDFS and then read from HDFS.
+ HDFSSource is the default ARCSource implementation.
+ HDFSSource can accept both directory and individual files as input files. Directories will be recursively scanned for regular files.
+ HDFSSource will check for input files using the P_INPUT variable (see HDFSExample). If it is null or empty it will check for input file given using the common [setInputPaths()](http://hadoop.apache.org/common/docs/current/api/org/apache/hadoop/mapred/FileInputFormat.html#setInputPaths(org.apache.hadoop.mapred.JobConf, java.lang.String))

## How to compile
In order to ensure a successful compilation of the library please modify the build.proprieties file and set the hadoop.path variable correctly.
Then simply invoke:

```
ant
```

You'll find ARCInputFormat.jar ready for use.

