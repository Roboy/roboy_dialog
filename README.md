# DialogSystem
[![Documentation Status](https://readthedocs.org/projects/roboydialog/badge/?version=latest)](http://roboydialog.readthedocs.io/en/master/?badge=latest)

- [DialogSystem](#dialogsystem)
    - [What is this Project](#what-is-this-project)
    - [How does it work](#how-does-it-work)
    - [Installation](#installation)
        - [Requirements](#requirements)
        - [Recommendations](#recommendations)
        - [Installing and Usage via Command Line](#installing-and-usage-via-command-line)
            - [Clone the Dialog Manager repository](#clone-the-dialog-manager-repository)
            - [Set Environmental Variables](#set-environmental-variables)
            - [Installing](#installing)
            - [Running the Conversation Manager](#running-the-conversation-manager)
            - [Running NLU only](#running-nlu-only)
        - [Installing and Usage via an IDE](#installing-and-usage-via-an-ide)
        - [Troubleshooting](#troubleshooting)
    - [Explanations](#explanations)
        - [Environmental Variables](#environmental-variables)
            - [ROS Master References](#ros-master-references)
            - [Neo4J References](#neo4j-references)
    - [Using the Google Word2Vec Model in NLU](#using-the-google-word2vec-model-in-nlu)
    - [How to extend the project](#how-to-extend-the-project)

## What is this Project

This repository contains a dialog system developed for the humanoid robot Roboy (roboy.org). 

## How does it work

The basic NLP architecture is designed as a pipeline. An input device (derived from roboy.io.InputDevice) is producing text, which is passed to a variety of linguistic analyzers (derived from roboy.linguistics.sentenceanalysis.Analyzer). This currently consists of a Tokenizer and a POS tagger (both in roboy.linguistics.sentenceanalysis.SentenceAnalyzer) but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools. The results of all these linguistics analyzers are collected together with the original text (roboy.linguistics.sentenceanalysis.Interpretation) and passed on to a state machine (states are derived from roboy.dialog.personality.states.State) within a personality class (derived from roboy.dialog.personality.Personality) that decides how to react to the utterance. In the future, the intentions (roboy.logic.Intention) determined by the state machine will then formulated into proper sentences or other actions (roboy.dialog.action.Action) by a module called Verbalizer. Currently, these actions are still directly created in the personality class. Finally, the created actions are sent to the corresponding output device (roboy.io.OutputDevice).

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

## Installation

### Requirements

- Apache Maven
- Java 8 (Oracle is prefered)
- Git
- [Neo4J](http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance)
- Working Internet Connection for downloading dependencies


### Recommendations
------------------

- Intellij
- At least 8GB of RAM
- Ubuntu (or variation) 16.04 LTS [1]
    - Needed for `ROS Kinetic`


> Note that `apt` is bugged with Ubuntu 16.04. See: https://bugs.launchpad.net/ubuntu/+source/appstream/+bug/1583845 if you get problems with `apt-update`.

### Installing and Usage via Command Line

#### Clone the Dialog Manager repository

`git clone https://github.com/Roboy/roboy_dialog -b master --recursive`

> Tip: If you wish to clone another branch, e.g. devel, just simply replace `master` with the branch's name.

#### Set [Environmental Variables](http://roboy-memory.readthedocs.io/en/latest/Usage/1_getting_started.html>) 

If not done so already. Your `.bashrc` should look like:

``` bash

    export ROS_MASTER_URI="http://127.0.0.1:11311"
    export ROS_HOSTNAME="127.0.0.1"
    export NEO4J_ADDRESS="bolt://127.0.0.1:7687"
    export NEO4J_USERNAME="neo4j"
    export NEO4J_PASSWORD="neo4jpassword"
    export REDIS_URI="redis://localhost:6379/0"
    export REDIS_PASSWORD="root"
```

#### Installing

Clean, Compile, Test, Install and praise Maven.
`mvn clean install`

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

> Attention:: Make sure the submodules are initialized.

1. Import Dialog System as a Maven project into the IDE
2. Download Maven Dependencies
3. Execute `roboy.dialog.ConversationManager` as the main class.

### Troubleshooting

See the [Troubleshooting Page](http://roboy-dialog.readthedocs.io/en/latest/Usage/9_troubleshooting.html) 

## Explanations

### Environmental Variables

#### ROS Master References

Dialog is tied into the Roboy architecture as a ROS node.
Therefore, make sure to set the following environment variables to meaningful values:

```bash
export ROS_HOSTNAME=local-hostname
export ROS_MASTER_URI=http://rosmaster:11311
```

If no remote development instance of ROS master is available, just run
`roscore` in a [docker container](http://wiki.ros.org/docker/Tutorials/Docker).

#### Neo4J References

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


## Using the Google Word2Vec Model in NLU

For a more complete but also much more memory-intensive Word Vector model,
the NLU module has the ability to parse the GoogleNews word vector collection,
which can be retrieved from [here](https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz).

In order to use it, store and extract it under `resources_nlu/word2vec`. Then just set
`WORD2VEC_GOOGLE: true` in `parser.properties`.

## How to extend the project

Pick the corresponding interface, depending on which part of the system you want to extend. If you want to add new devices go for the input or output device interfaces. If you want to extend the linguistic analysis implement the Analyzer interface or extend the SentenceAnalyzer class. If you are happy with input, linguistics and output and just want to create more dialog, implement the Personality interface.

|Create a new ...|By implementing ...   |
|----------------|-----------------------|
|Input Device    |roboy.io.InputDevice|
|NLP Analyzer    |roboy.linguistics.sentenceanalysis.Analyzer|
|State Machine   |roboy.dialog.personality.Personality|
|State           |roboy.dialog.personality.states.State|
|Action type     |roboy.dialog.action.Action|
|Output device   |roboy.io.OutputDevice|

The interfaces are deliberately simple, containing only 0 - 2 methods that have to be implemented. Once you implemented your new classes include them in the personality used in roboy.dialog.DialogSystem, if you only implemented single states or directly in roboy.dialog.DialogSystem for everything else.
