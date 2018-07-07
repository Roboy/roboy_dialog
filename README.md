# DialogSystem
[![Documentation Status](https://readthedocs.org/projects/roboydialog/badge/?version=latest)](http://roboydialog.readthedocs.io/en/master/?badge=latest)

- [DialogSystem](#dialogsystem)
    - [What is this Project](#what-is-this-project)
    - [Does This Project Have Documentation](#does-this-project-have-documentation)
    - [How does the Dialog System Work](#how-does-the-dialog-system-work)
    - [Installation](#installation)
        - [Quick-Install](#quick-install)
        - [Requirements](#requirements)
        - [Recommendations](#recommendations)
        - [Optional](#optional)
        - [Installing and Usage via Command Line](#installing-and-usage-via-command-line)
            - [Neo4J](#neo4j)
            - [Clone the Dialog Manager repository](#clone-the-dialog-manager-repository)
            - [Set Environmental Variables](#set-environmental-variables)
            - [Installing](#installing)
            - [Running the Conversation Manager](#running-the-conversation-manager)
            - [Running NLU only](#running-nlu-only)
        - [Installing and Usage via an IDE](#installing-and-usage-via-an-ide)
        - [Troubleshooting](#troubleshooting)
    - [Environmental Variables](#environmental-variables)
        - [ROS Master References](#ros-master-references)
        - [Neo4J References](#neo4j-references)
        - [Redis References](#redis-references)
        - [Example](#example)
    - [Configuration of Roboy_Dialog](#configuration-of-roboydialog)
    - [Using the Google Word2Vec Model in NLU](#using-the-google-word2vec-model-in-nlu)

## What is this Project

This repository contains a dialog system developed for the humanoid robot [Roboy](roboy.org).

## Does This Project Have Documentation

Yes, you can find them [here](https://roboy-dialog.readthedocs.io/).

## How does the Dialog System Work

The basic NLP architecture is designed as a pipeline. An input device (derived from roboy.io.InputDevice) is producing text, which is passed to a variety of linguistic analyzers (derived from roboy.linguistics.sentenceanalysis.Analyzer). This currently consists of a Tokenizer and a POS tagger (both in roboy.linguistics.sentenceanalysis.SentenceAnalyzer) but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools. The results of all these linguistics analyzers are collected together with the original text (roboy.linguistics.sentenceanalysis.Interpretation) and passed on to a state machine (states are derived from roboy.dialog.personality.states.State) within a personality class (derived from roboy.dialog.personality.Personality) that decides how to react to the utterance. In the future, the intentions (roboy.logic.Intention) determined by the state machine will then formulated into proper sentences or other actions (roboy.dialog.action.Action) by a module called Verbalizer. Currently, these actions are still directly created in the personality class. Finally, the created actions are sent to the corresponding output device (roboy.io.OutputDevice).

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

## Installation

### Quick-Install

The quick guide for people who don't care about any of the technical mumbo jumbo. This is a local installation guide.

[Set Environmental Variables](#environmental-variables). **Only do this once.**

```bash
sudo apt-get install maven openjdk-9-jdk git docker.io

sudo docker run --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j/data:/data --volume=$HOME/neo4j/logs:/logs neo4j:3.4

git clone https://github.com/Roboy/roboy_dialog -b devel --recursive

cd roboy_dialog

mvn clean install

java -Xmx6g -d64 -cp dialog/target/roboy-dialog-system-2.1.9-jar-with-dependencies.jar roboy.dialog.DialogSystem
```

### Requirements

- Apache Maven
- Java 8 (Oracle is preferred)
- Git
- [Neo4J](http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance)
- Working Internet Connection for downloading dependencies

### Recommendations

- Intellij
- At least 8GB of RAM
    - This project will run with a 4GB of RAM, however development will be a horror-show.
- Ubuntu (or variation) 16.04 LTS
    - Needed for `ROS Kinetic`
    - If you do not need ROS features, any version of Linux should do
- At least 6GB of Disk Space
    - ~ 4GB for Maven Dependencies
    - ~ 500MB for Roboy_Dialog with all sub-modules and files generated via mvn clean install
    - Rest is a ballpark estimate for Neo4J, Redis, ROS and their dependencies

> Note that `apt` is bugged with Ubuntu 16.04. See: https://bugs.launchpad.net/ubuntu/+source/appstream/+bug/1583845 if you get problems with `apt-update`.

### Optional

- ROS
- Redis

### Installing and Usage via Command Line

#### Neo4J

If you have not, install [Neo4J](http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance). Neo4J **must** be running or else the tests will fail. The easiest way to start Neo4J is via `docker.io`. Simply run...

``` bash
sudo docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --volume=$HOME/neo4j/logs:/logs \
    neo4j:3.4
```

#### Clone the Dialog Manager repository

`git clone https://github.com/Roboy/roboy_dialog -b master --recursive`

Enter your project directory with `cd roboy_dialog`

> Tip: If you wish to clone another branch, e.g. `devel`, just simply replace `master` with the branch's name.

#### Set Environmental Variables

Set the environmental variables described [here](#environmental-variables)

#### Installing

Clean, Compile, Test, Install and praise Maven.
`mvn clean install`

> To be done in the project root directory, this POM calls all other POMs.

#### Running the Conversation Manager

`mvn exec:java -Dexec.mainClass="roboy.dialog.ConversationManager"`

or...

``` bash
java -Xmx6g -d64 -cp dialog/target/roboy-dialog-system-2.1.9-jar-with-dependencies.jar \
    roboy.dialog.DialogSystem
```

#### Running NLU only

``` bash
java -Xmx6g -d64 -cp \
    nlu/parser/target/roboy-parser-2.0.0-jar-with-dependencies.jar \
    edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface.java
```

### Installing and Usage via an IDE

Clone the Dialog Manager repository either using your IDE's VCS Tools or using the command line.
`git clone https://github.com/Roboy/roboy_dialog`

> Attention:: Make sure the sub-modules are initialized.

1. Import Dialog System as a Maven project into the IDE
2. Download the Maven Dependencies
3. Execute `roboy.dialog.ConversationManager` as the main class.

**Do not forget to start [Neo4J](#neo4j)!**

### Troubleshooting

See the [Troubleshooting Page](http://roboy-dialog.readthedocs.io/en/latest/Usage/9_troubleshooting.html)

## Environmental Variables

One needs to set environmental variables, to tell roboy_dialog where specific services are located. These are passed into the program and used to set the variables...

```java
public final static String ROS_MASTER_URI;
public final static String ROS_HOSTNAME;
public final static String NEO4J_ADDRESS;
public final static String NEO4J_USERNAME;
public final static String NEO4J_PASSWORD;
public final static String REDIS_URI;
public final static String REDIS_PASSWORD;
```

One does this by adding references to your `.bashrc` or `.bash_profile`, that `roboy_dialog` shall read from...

``` bash
export ROS_MASTER_URI="***"
export ROS_HOSTNAME="***"
export NEO4J_ADDRESS="***"
export NEO4J_USERNAME="***"
export NEO4J_PASSWORD="***"
export REDIS_URI="***"
export REDIS_PASSWORD="***"
```

See [here](#example) for an example.

### ROS Master References

Dialog is tied into the Roboy architecture as a ROS node.
Therefore, make sure to set the following environment variables to meaningful values:

```bash
export ROS_HOSTNAME=local-hostname
export ROS_MASTER_URI=http://rosmaster:11311
```

If no remote development instance of ROS master is available, just run
`roscore` in a [docker container](http://wiki.ros.org/docker/Tutorials/Docker).

### Neo4J References

The dialog system's memory module uses Neo4j, a graph database which is
stores relations between entities observed by roboy (names, hobbies, locations etc.).
Therefore, make sure to set the following environment variables to meaningful values:
``bash
export NEO4J_ADDRESS=bolt://my-neo4j-database:7687
export NEO4J_USERNAME=user
export NEO4J_PASSWORD=pass
``

If no remote development instance of Neo4j is available, just run
`Neo4j` in a [docker container](https://neo4j.com/developer/docker/#_how_to_use_the_neo4j_docker_image). For more options and additional information, refer to `docs/Usage` in the
memory module.

### Redis References

Redis is a software used for face features storage on a remote server. In most cases, you can simply ignore this.

```bash
export REDIS_URI="***"
export REDIS_PASSWORD="***"
```

### Example

Here is an example, if one were to want to locally run `roboy_dialog`. In most cases you would not even need the last two lines.
``` bash
export ROS_MASTER_URI="http://127.0.0.1:11311"
export ROS_HOSTNAME="127.0.0.1"
export NEO4J_ADDRESS="bolt://127.0.0.1:7687"
export NEO4J_USERNAME="neo4j"
export NEO4J_PASSWORD="neo4jpassword"
export REDIS_URI="redis://localhost:6379/0"
export REDIS_PASSWORD="root"
```

## Configuration of Roboy_Dialog

One is able to customize the modules that are enabled, when dialog starts. One does this by altering the options in `config.properties`. For more details, see the [detailed documentation page](https://roboydialog.readthedocs.io/en/devel/user_manual/2_configuration.html#configuration).

## Using the Google Word2Vec Model in NLU

For a more complete but also much more memory-intensive Word Vector model,
the NLU module has the ability to parse the GoogleNews word vector collection,
which can be retrieved from [here](https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz).

In order to use it, store and extract it under `resources_nlu/word2vec`. Then just set
`WORD2VEC_GOOGLE: true` in `parser.properties`.