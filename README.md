# Roboy Dialog System

[![Documentation Status](https://readthedocs.org/projects/roboydialog/badge/?version=latest)](http://roboydialog.readthedocs.io/en/master/?badge=latest)

- [Roboy Dialog System](#roboy-dialog-system)
    - [What is this Project](#what-is-this-project)
    - [Installation Guide](#installation-guide)
        - [Requirements](#requirements)
        - [Recommendations](#recommendations)
        - [Command Line Installation Instructions](#command-line-installation-instructions)
        - [IDE Installation Instructions](#ide-installation-instructions)
    - [Environment Variables](#environment-variables)
        - [Neo4j](#neo4j)
        - [ROS-master](#ros-master)
        - [Redis](#redis)
    - [Running the Dialog System](#running-the-dialog-system)
    - [Running NLU only](#running-nlu-only)
        - [Using the Google Word2Vec Model in NLU](#using-the-google-word2vec-model-in-nlu)
    - [Troubleshooting](#troubleshooting)
    - [Configuration of Roboy_Dialog](#configuration-of-roboydialog)

## What is this Project

This repository contains a dialog system developed for the humanoid robot [Roboy](roboy.org). One can find more information about this project through its [documentation](https://readthedocs.org/projects/roboydialog/).

## Installation Guide

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

### Recommendations

- Intellij
- At least 8GB of RAM
- At least 6GB of Disk Space
    - ~ 4GB for Maven Dependencies
    - ~ 500MB for Roboy_Dialog with all sub-modules and files generated via mvn clean install
    - Rest is a ballpark estimate for Neo4J, Redis, ROS and their dependencies
- Ubuntu (or variation) 16.04 LTS or Mac OSX
    - Needed for `ROS Kinetic`
    - If you do not need ROS features, any version of Linux should do

### Command Line Installation Instructions

[Set up Neo4j](#neo4j).

```bash
# Install Maven, Java, Docker and other programs needed
sudo apt-get install maven openjdk-8-jdk git docker.io make wget zip
# Download and Run Neo4J with Docker
sudo docker run --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j/data:/data --volume=$HOME/neo4j/logs:/logs neo4j:3.4
# Clone Dialog's Master Branch (replace master with devel for other branches)
git clone https://github.com/Roboy/roboy_dialog --recursive -b master
# Change Directory to your new clone
cd roboy_dialog
# Download Dependencies and Install
mvn clean install
# Use this to Start Dialog
./start.sh
```

### IDE Installation Instructions

Clone the Dialog Manager repository either using your IDE's VCS Tools or using the command line.
`git clone https://github.com/Roboy/roboy_dialog`

*Attention: Make sure that the git sub-modules are initialized!*

1. Import Dialog System as a Maven project into the IDE
2. Download the Maven Dependencies
3. Execute `roboy.dialog.ConversationManager` as the main class.

**Do not forget to start [Neo4J](#neo4j)!**

## Environment Variables

You need to set environment variables to tell `roboy_dialog` where Neo4j, ROS (optional) and Redis (optional) are located. In most cases, it shall suffice just to set the Neo4J variables. Just add the `export VARIABLE=value` statements to your `$HOME/.bashrc`.

Since all of these dependencies are actually required by `roboy_memory`, you can find more detailed installation instructions in [the memory docs](https://roboy-memory.readthedocs.io/en/latest/Usage/1_getting_started.html#configuring-the-package).

### Neo4j

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

### ROS-master

**Note: Running ROS is only necessary when running `roboy_dialog` in the Roboy architecture. Otherwise, you may also set `ROS_ENABLED: false` in `config.properties`.**

Dialog is tied into the Roboy architecture as a ROS node.
Therefore, make sure to set the following environment variables to meaningful values:

```bash
export ROS_HOSTNAME=local-hostname
export ROS_MASTER_URI=http://rosmaster:11311
```

If no remote development instance of ROS master is available, just run
`roscore` in a [docker container](http://wiki.ros.org/docker/Tutorials/Docker).

### Redis

Redis is a software used for facial feature-storage on a remote server. In most cases, you can simply ignore this, as the average project **does not** need this.

```bash
export REDIS_URI="***"
export REDIS_PASSWORD="***"
```

## Running the Dialog System

Once the Neo4j (and ROS) dependencies are satisfied, run the dialog system via ...

```bash
./start.sh
```

## Running NLU only

```bash
java -Xmx6g -d64 -cp \
    nlu/parser/target/roboy-parser-2.0.0-jar-with-dependencies.jar \
    edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface.java
```

### Using the Google Word2Vec Model in NLU

For a more complete but also much more memory-intensive Word Vector model,
the NLU module has the ability to parse the GoogleNews word vector collection,
which can be retrieved from [here](https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz).

In order to use it, store and extract it under `resources_nlu/word2vec`. Then just set
`WORD2VEC_GOOGLE: true` in `parser.properties`.

## Troubleshooting

See the [Troubleshooting Page](http://roboydialog.readthedocs.io/en/devel/Usage/9_troubleshooting.html)

## Configuration of Roboy_Dialog

One is able to customize the modules that are enabled, when dialog starts. One does this by altering the options in `config.properties`. For more details, see the [detailed documentation page](https://roboydialog.readthedocs.io/en/devel/user_manual/2_configuration.html#configuration

