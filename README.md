# DialogSystem
[![Documentation Status](https://readthedocs.org/projects/roboydialog/badge/?version=latest)](http://roboydialog.readthedocs.io/en/master/?badge=latest)

## What is it

This repository contains a dialog system developed for the humanoid robot Roboy (roboy.org).

## How does it work

The basic NLP architecture is designed as a pipeline. An input device (derived from de.roboy.io.InputDevice) is producing text, which is passed to a variety of linguistic analyzers (derived from de.roboy.linguistics.sentenceanalysis.Analyzer). This currently consists of a Tokenizer and a POS tagger (both in de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer) but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools. The results of all these linguistics analyzers are collected together with the original text (de.roboy.linguistics.sentenceanalysis.Interpretation) and passed on to a state machine (states are derived from de.roboy.dialog.personality.states.State) within a personality class (derived from de.roboy.dialog.personality.Personality) that decides how to react to the utterance. In the future, the intentions (de.roboy.logic.Intention) determined by the state machine will then formulated into proper sentences or other actions (de.roboy.dialog.action.Action) by a module called Verbalizer. Currently, these actions are still directly created in the personality class. Finally, the created actions are sent to the corresponding output device (de.roboy.io.OutputDevice).

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

## How to run it

The repository contains a project that can be readily imported into Eclipse. Best use the Git Eclipse Plugin to check it out. The code can be executed by running de.roboy.dialog.DialogSystem.

### Requirements

- Apache Maven
- Java 8 (Oracle is preferred)
- `ruby` 1.8.7 or 1.9
- `git`
- [Neo4J](http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance)
- Working Internet Connection for downloading dependencies
- `wget`
- `make`
- `zip`

*Make sure that you are using Java 1.8 both for* `java` *and* `javac` *! You can check this by running*
```bash
javac -version
java -version
```

### Quick installation snippet

[Set up Neo4j](#neo4j).

```bash
sudo apt-get install maven openjdk-9-jdk git docker.io

sudo docker run --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j/data:/data --volume=$HOME/neo4j/logs:/logs neo4j:3.4

git clone https://github.com/Roboy/roboy_dialog --recursive

cd roboy_dialog

mvn clean install

./start.sh
```
### Recommendations

- Intellij
- At least 8GB of RAM
- At least 6GB of Disk Space
    - ~ 4GB for Maven Dependencies
    - ~ 500MB for Roboy_Dialog with all sub-modules and files generated via mvn clean install
    - Rest is a ballpark estimate for Neo4J, Redis, ROS and their dependencies
- Ubuntu (or variation) 16.04 LTS
    - Needed for `ROS Kinetic`
    - If you do not need ROS features, any version of Linux should do

### External dependencies

You need to set environment variables to tell `roboy_dialog`
where Neo4j, ROS (optional) and Redis (optional) are located.

#### Neo4j

The dialog system's memory module uses Neo4j, a graph database which is
stores relations between enttities observed by roboy (names, hobbies, locations etc.).
Therefore, make sure to set the following environment variables to meaningful values:

```bash
export NEO4J_ADDRESS=bolt://my-neo4j-database:7687
export NEO4J_USERNAME=user
export NEO4J_PASSWORD=pass
```

If no remote development instance of Neo4j is available, just run
Neo4j in a [docker container](https://neo4j.com/developer/docker/#_how_to_use_the_neo4j_docker_image).
For more options and additional information, refer to `docs/Usage` in the
memory module.

``` bash
sudo docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --volume=$HOME/neo4j/logs:/logs \
    neo4j:3.4
```

#### ROS-master

**Note: Running ROS is only necessary when running `roboy_dialog` in the Roboy architecture. Otherwise, you may also set `ROS_ENABLED: false` in `config.properties`.**

Dialog is tied into the Roboy architecture as a ROS node.
Therefore, make sure to set the following environment variables to meaningful values:

```bash
export ROS_HOSTNAME=local-hostname
export ROS_MASTER_URI=http://rosmaster:11311
```

If no remote development instance of ROS master is available, just run
`roscore` in a [docker container](http://wiki.ros.org/docker/Tutorials/Docker).

#### Redis

Redis is a software used for facial feature-storage on a remote server. In most cases, you can simply ignore this.

```bash
export REDIS_URI="***"
export REDIS_PASSWORD="***"
```

### Running the Dialog System

Once the Neo4j (and ROS) dependencies are satisfied, run the dialog system via ...

```bash
./start.sh
```

#### Running NLU only

```bash
java -Xmx6g -d64 -cp \
    nlu/parser/target/roboy-parser-2.0.0-jar-with-dependencies.jar \
    edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface.java
```

### Installation and Usage via IDE

Clone the Dialog Manager repository either using your IDE's VCS Tools or using the command line.
`git clone https://github.com/Roboy/roboy_dialog`

*Attention: Make sure that the git sub-modules are initialized!*

1. Import Dialog System as a Maven project into the IDE
2. Download the Maven Dependencies
3. Execute `roboy.dialog.ConversationManager` as the main class.

**Do not forget to start [Neo4J](#neo4j)!**

### Troubleshooting

See the [Troubleshooting Page](http://roboy-dialog.readthedocs.io/en/latest/Usage/9_troubleshooting.html)

#### Using the Google Word2Vec Model in NLU

For a more complete but also much more memory-intensive Word Vector model,
the NLU module has the ability to parse the GoogleNews word vector collection,
which can be retrieved from [here](https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz).

In order to use it, store and extract it under `resources_nlu/word2vec`. Then just set
`WORD2VEC_GOOGLE: true` in `parser.properties`.

## How to extend it

Pick the corresponding interface, depending on which part of the system you want to extend. If you want to add new devices go for the input or output device interfaces. If you want to extend the linguistic analysis implement the Analyzer interface or extend the SentenceAnalyzer class. If you are happy with input, linguistics and output and just want to create more dialog, implement the Personality interface.

|Create a new ...|By implementing ...   |
|----------------|-----------------------|
|Input Device    |de.roboy.io.InputDevice|
|NLP Analyzer    |de.roboy.linguistics.sentenceanalysis.Analyzer|
|State Machine   |de.roboy.dialog.personality.Personality|
|State           |de.roboy.dialog.personality.states.State|
|Action type     |de.roboy.dialog.action.Action|
|Output device   |de.roboy.io.OutputDevice|

The interfaces are deliberately simple, containing only 0 - 2 methods that have to be implemented. Once you implemented your new classes include them in the personality used in de.roboy.dialog.DialogSystem, if you only implemented single states or directly in de.roboy.dialog.DialogSystem for everything else.
