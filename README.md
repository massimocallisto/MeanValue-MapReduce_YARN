## MeanValue-MapReduce_YARN

The project contains simple Maven-based examples that implements: 1) the classic wordcount example from Hadoop, 2) a mapreduce job about the computation of average measurements of IoT devices.
The project contains also the dbms export example. See `export-dbms` folder.

The examples are part od the master course on Big Data at University of Camerino.

## Requirements

- Maven 3.3.x and Java JDK 8 (optional if you use the existing jar)
- A Hadoop Yarn installation (we are going to use the one dicusses during the teaching course)

## Word count example

- The runner class is `bigdata.hadoop.mapreduce.wordcount.App`
- The runner takes as input 2 parameters:
  - the input hdfs file (or folder)
  - a hdfs output folder to store the job results
- Based on the input file, the job output a couple of files which contain the extracted words and their occurrences. Note the a  word must be present only once among the files.

## Mean values example

- The runner class is `bigdata.hadoop.mapreduce.mean.App`
- The runner also implements internally the Map and Reduce Class
- The runner takes as input 2 parameters:
  - the input hdfs file (or folder)
  - a hdfs output folder to store the job results
- Based on the input file, the job output a couple of files which contain the `day-measure` and the corresponding average. Note the each daytime-measure must be present only once among the files.

Note that the above example is strictly related to the CSV file [input_csv/2020_04_01T00_00_00_2020_05_01T00_00_00.csv](input_csv/2020_04_01T00_00_00_2020_05_01T00_00_00.csv). Changing the file structure also requires rewriting the aggregation logic.

### 1a - Project build

If you do not want to build the project, see the next section below. Otherwise, from the terminal run the maven compile command:

    mvn clean package

The output should be the `target` folder with a jar file named `meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar`

### 1b - Project build

The existing file [meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar](maven_build_output/meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar) within the `maven_build_output` folder already contains the FAT jar ready to run.


### 2 - Upload file and prepare the data

**Note**: replace the IP address with yours.

You should copy the jar file into the VM image and then inside the container. Supposing your VM has IP 192.68.17.129 (user ubuntu) you can upload the jar file into /tmp via ssh from terminal as follow:

    scp meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar ubuntu@192.168.17.129:/tmp
    scp input_csv/2020_04_01T00_00_00_2020_05_01T00_00_00.csv ubuntu@192.168.17.129:/tmp
    scp input/SampleTextFile_200kb.txt ubuntu@192.168.17.129:/tmp

Then login inside the VM and copy the jar file into the namenode container (the node that has the yarn libraries and commands already installed).

move to /tmp and use this command to copy the jar file inside the container:

    docker cp meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar namenode:/tmp
    docker cp 2020_04_01T00_00_00_2020_05_01T00_00_00.csv namenode:/tmp
    docker cp SampleTextFile_200kb.tx namenode:/tmp

Login into the namenode container:

    docker exec -ti namenode bash

Generate some text and upload into Hadoop:

    hadoop fs -mkdir /input
    hadoop fs -copyFromLocal SampleTextFile_200kb.txt /input/

Also:

    hadoop fs -mkdir /input_csv
    hadoop fs -copyFromLocal 2020_04_01T00_00_00_2020_05_01T00_00_00.csv /input_csv/

### 3 - Job execution

Supposing you are inside the docker container named namenode and you copied the jar file under /tmp within the container, then use the following command to execute the job.
For the wordcount example:

    yarn jar meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar bigdata.hadoop.mapreduce.wordcount.App /input /out

For the mean values computation:

    yarn jar meanvalue-1.0-SNAPSHOT-jar-with-dependencies.jar bigdata.hadoop.mapreduce.mean.App /input_csv /out_csv

![](img/1.png)

Check Yarn interface while it is running:

![](img/2.png)

When the job is completed you can check the hdfs file system ([http://192.168.17.129:50070/explorer.html#/](http://192.168.17.129:50070/explorer.html#/)):

![](img/3.png)

Then you can download the output files:

![](img/4.png)

When you are downloading the file, the url will contain the container name; replace it with VM ip from:

![](img/5.png)

to

[http://192.168.17.129:50075/webhdfs/](http://192.168.17.129:50075/webhdfs/v1/out/wordcount/part-r-00000?op=OPEN&namenoderpcaddress=namenode:9000&offset=0)….



### 4 - Extensions

You can update the job in order to persist final result in some database.
Use the project `export-dbms` for details.