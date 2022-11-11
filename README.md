## Wordcount-MapReduce\_YARN

This Maven-based example implements the classic wordcount example from Hadoop

The example:

- Implement the runner class as bigdata.hadoop.mapreduce.App
- The runner takes as input 2 parameters:
  - the input file (or folder)
  - an output folder where to store the job results
- Based on the input file, the job provides at the end some text files where words are being followed by their occurrences.

The project contains also the dbms export example. See `export-dbms` folder.

**Requirements**

- Maven 3.3.x
- Java JDK 8
- A Hadoop Yarn installation (we are using the one described during lesson lab and based on Docker)

### 1 - Project build

Download and unpack the project zip. From the terminal move inside the unpacked folder and run the maven compile command:

    mvn clean package

The output should be a target folder with a jar file named wordcount-1.0-SNAPSHOT.jar

### 2 - Upload file and prepare the data

You should copy the jar file into the VM image and then inside the container. Supposing your VM has IP 192.68.17.129 (user ubuntu) you can upload the jar file into /tmp via ssh from terminal as follow:

    scp meanvalue-1.0-SNAPSHOT.jar ubuntu@192.168.17.129:/tmp
    scp input_csv/2020_04_01T00_00_00_2020_05_01T00_00_00.csv ubuntu@192.168.17.129:/tmp

Then login inside the VM and copy the jar file into the namode container (the node that has the yarn libraries and commands already installed).

move to /tmp and use this command to copy the jar file inside the container:

    docker cp meanvalue-1.0-SNAPSHOT.jar namenode:/tmp
    docker cp 2020_04_01T00_00_00_2020_05_01T00_00_00.csv namenode:/tmp

Login into the namenode container:

    docker exec -ti namenode bash

Generate some text and upload into Hadoop:

    hadoop fs -mkdir /input
    hadoop fs -copyFromLocal 2020_04_01T00_00_00_2020_05_01T00_00_00.csv /input/

### 3 - Job execution

Supposing you are inside the docker container named namenode and you copied the jar file under /tmp within the container, then use the following command to execute the job:

    yarn jar meanvalue-1.0-SNAPSHOT.jar bigdata.hadoop.mapreduce.mean.App /input /out

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


[1](#sdfootnote1anc)[https://hadoop.apache.org/docs/r1.2.1/mapred\_tutorial.html](https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html)

[2](#sdfootnote2anc)[https://drive.google.com/file/d/19ExMesOVtTtHLEmqTh\_ptrW-OQLWdqrT/view?usp=sharing](https://drive.google.com/file/d/19ExMesOVtTtHLEmqTh_ptrW-OQLWdqrT/view?usp=sharing)


### 4 - Extensions

You can update the job in order to persist final result in some database.
Use the project `export-dbms` for details.