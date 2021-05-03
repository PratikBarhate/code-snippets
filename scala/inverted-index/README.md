## Inverted index

Small code snippet for creating inverted index from the text files, with few small performance tweaks.

### Requirements

1. JDK 8
2. Maven

### Target Directory Structure 

The built application `tar` contains 4 directories
1. `bin` directory contains a shell script to execute the application.
2. `conf` directory contains two files:- one to configure the number of 
part files (`output_conf.json`) for the outputs and the another to specify 
spark configuration externally (`spark_conf.json`).
4. `lib` directory contains a `jar` of the application.

* _NOTE:_ Within the `spark_conf.json`, you can provide additional JVM parameter
`-XX:+UseStringDeduplication` through the key `spark.executor.extraJavaOptions`;
all values should be space separated e.g `-XX:+UseG1GC -XX:+UseStringDeduplication`

* _NOTE:_ The output indexes are current persisted in string format. It can be
stored using more efficient file format if the downstream applications are known.