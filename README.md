This project extracts from the original commoncrawl project only the ARCInputFormat class and its dependencies. It also implement a new ARCSource, HDFSSource, which allows ARC files to be read from HDFS.

## How to compile
In order to ensure a successful compilation of the library please modify the build.proprieties file and set the hadoop.path variable correctly.
Then simply invoke:

```
ant
```

You'll find ARCInputFormat.jar ready for use.
