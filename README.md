# RC-Project-Spark

This repository contains scripts to analyse the network of Users-Subreddits of [reddit](reddit.com), using [Apache Spark](https://spark.apache.org/).

## Meaningful Scripts

- [`CreateUserBuckets`](https://github.com/DReigada/CN-Project/blob/master/src/main/scala/part2/CreateUserBuckets.scala)
    * Create itemset buckets for using in [this script](https://github.com/DReigada/RC-Project-Spark/blob/master/src/main/scala/rc/FindSubredditsItemSet.scala)

- [`ReplaceNames`](https://github.com/DReigada/CN-Project/blob/master/src/main/scala/part2/ReplaceNames$.scala)
    * Changes the Subreddit ids to names for the output of [this script](https://github.com/DReigada/RC-Project-Spark/blob/master/src/main/scala/rc/FindSubredditsItemSet.scala)

- [`ParseAssocRulesReplacingNames`](https://github.com/DReigada/CN-Project/blob/master/src/main/scala/part2/ParseAssocRulesReplacingNames$$.scala)
    * Changes the Subreddit ids to names for the output of [this script](https://github.com/DReigada/RC-Project-Spark/blob/master/src/main/scala/rc/GenerateAssociations.scala)

## Requirements
- Java 8 JDK
- scala (https://www.scala-lang.org/)
- sbt (http://www.scala-sbt.org/)
    
    
## Running

1. Generate the binaries with `sbt universal:packageZipTarball`
2. Uncompress the generated file located at `target/universal/cn-project-0.1.0-SNAPSHOT.tgz`
3. Run the script inside `<extractedDirectory>/bin` that has the same name as the scala script you want to run
