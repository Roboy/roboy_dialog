# Roboy Dialog System

[![Documentation Status](https://readthedocs.org/projects/roboydialog/badge/?version=latest)](http://roboydialog.readthedocs.io/en/master/?badge=latest)

- [Roboy Dialog System](#roboy-dialog-system)
    - [What is this Project](#what-is-this-project)
    - [Installation Guide](#installation-guide)
        - [Requirements](#requirements)
        - [Recommendations](#recommendations)
        - [Command Line Installation Instructions](#command-line-installation-instructions)
            - [Running Neo4J's Docker Image as Non-Root](#running-neo4js-docker-image-as-non-root)
            - [Installation without Neo4J Tests](#installation-without-neo4j-tests)
        - [IDE Installation Instructions](#ide-installation-instructions)
    - [Environment Variables](#environment-variables)
        - [Neo4j](#neo4j)
        - [ROS-master](#ros-master)
        - [Redis](#redis)
    - [Running the Dialog System](#running-the-dialog-system)
    - [Running NLU only](#running-nlu-only)
        - [Using the Google Word2Vec Model in NLU](#using-the-google-word2vec-model-in-nlu)
    - [Configuration of roboy_dialog](#configuration-of-roboydialog)
        - [Logging Levels](#logging-levels)
        - [Enabling External APIs](#enabling-external-apis)
    - [Troubleshooting](#troubleshooting)

## What is this Project

![Roboy says hi!](/docs/img/roboycuteface.png?raw=true "Roboy_greeting")

This repository contains a dialog system developed for the humanoid robot [Roboy](roboy.org). One can find more information about this project through its [documentation](https://readthedocs.org/projects/roboydialog/).

## Installation Guide

### Requirements

- Apache Maven
- Java 8 (Oracle is preferred)
- `ruby` (1.8.7 or 1.9 advised)
- `git`
- [Neo4J](http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance)
- `wget`
- `make`
- `zip`
- Working Internet Connection for downloading dependencies!

*Make sure that you are using Java 1.8 both for* `java` *and* `javac` *! You can check this by running*
```bash
javac -version
java -version
```

### Recommendations

- [Intellij IDEA](https://www.jetbrains.com/idea/ "IDEA's Homepage")
- At least 8GB of RAM
- At least 6GB of Disk Space
    - ~ 4GB for Maven Dependencies
    - ~ 500MB for Roboy_Dialog with all sub-modules and files generated via `mvn clean install`
    - Rest is a ballpark estimate for Neo4J, Redis, ROS and their dependencies
- Ubuntu (or variation) 16.04 LTS or Mac OSX
    - Needed for `ROS Kinetic`
    - If you do not need ROS features, any version of Linux should do

### Command Line Installation Instructions

Assumes you have already [set up Neo4j](#neo4j).

```bash
# Install Maven, Java, Docker and other programs needed (installing openJDK because of simplicity)
sudo apt-get install maven openjdk-8-jdk git docker.io make wget zip ruby
# Download and Run Neo4J with Docker
sudo docker run --publish=7474:7474 --publish=7687:7687 --volume=$HOME/neo4j/data:/data --volume=$HOME/neo4j/logs:/logs neo4j:3.0
# Clone Dialog's Master Branch (replace master with devel for other branches)
git clone https://github.com/Roboy/roboy_dialog --recursive -b master
# Change Directory to your new clone
cd roboy_dialog
# Download Dependencies and Install
mvn clean install
# Use this to Start Dialog
./start.sh
```

#### Running Neo4J's Docker Image as Non-Root

See [here](https://neo4j.com/docs/operations-manual/current/installation/docker/#docker-user) for more information.

#### Installation without Neo4J Tests

If you do not require Neo4J, or otherwise wish to prevent Neo4J-dependent tests execute (ie. situations with no internet connection and only remote instance setup), you can have the Neo4J tests ignored, by simply appending `-D neo4jtest=false` to your maven command.

Example: `mvn clean install -D neo4jtest=false`, `mvn test -D neo4jtest=false`

This will disable `roboy_memory`'s `org.roboy.memory.util.Neo4jTest` & `dialog`'s `roboy.memory.MemoryIntegrationTest`, since both require an active Neo4J database instance.

### IDE Installation Instructions

Clone the Dialog Manager repository either using your IDE's VCS Tools or using the command line.
`git clone --recursive https://github.com/Roboy/roboy_dialog`

*Attention: Make sure that the git sub-modules are initialized!*

1. Import Dialog System as a Maven project into the IDE
2. Download the Maven Dependencies
3. Execute `roboy.dialog.ConversationManager` as the main class.

**Do not forget to start [Neo4J](#neo4j)!**

## Environment Variables

You need to set environment variables to tell `roboy_dialog` where Neo4j, ROS (optional) and Redis (optional) are located. In most cases, it will suffice just to set the Neo4J variables. Just add the `export VARIABLE=value` statements to your `$HOME/.bashrc`.

Since all of these dependencies are actually required by `roboy_memory`, you can find more detailed installation instructions in [the memory docs](https://roboy-memory.readthedocs.io/en/latest/Usage/1_getting_started.html#configuring-the-package).

### Neo4j

The dialog system's memory module uses Neo4j, a graph database which stores relations between enttities observed by roboy (names, hobbies, locations etc.).Therefore, make sure to set the following environment variables to meaningful values:

```bash
export NEO4J_ADDRESS=bolt://NEO4J-ADDRESS-GOES-HERE:7687
export NEO4J_USERNAME=user
export NEO4J_PASSWORD=pass
```

If no remote development instance of Neo4j is available, just run Neo4j in a [docker container](https://neo4j.com/developer/docker/#_how_to_use_the_neo4j_docker_image). For more options and additional information, refer to `docs/Usage` in the memory module.

``` bash
sudo docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --volume=$HOME/neo4j/logs:/logs \
    neo4j:3.0
```

### ROS-master

**Note: Running ROS is only necessary when running `roboy_dialog` within the Roboy architecture. Otherwise, you may also set `ROS_ENABLED: false` in `config.properties`.**

Dialog is tied into the Roboy architecture as a ROS node. Therefore, make sure to set the following environment variables to meaningful values:

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

If everything is running and configured correctly, you should be able to have a basic conversation like following

![Roboy says hi!](/docs/img/roboydialog.png?raw=true "Roboy_greeting")

## Running NLU only

```bash
java -Xmx6g -d64 -cp \
    nlu/parser/target/roboy-parser-2.0.0-jar-with-dependencies.jar \
    edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface.java
```

### Using the Google Word2Vec Model in NLU

For a more complete but also much more memory-intensive Word Vector model, the NLU module has the ability to parse the GoogleNews word vector collection, which can be retrieved from [here](https://s3.amazonaws.com/dl4j-distribution/GoogleNews-vectors-negative300.bin.gz).

In order to use it, store and extract it under `resources_nlu/word2vec`. Then just set `WORD2VEC_GOOGLE: true` in `parser.properties`.

## Configuration of roboy_dialog

One is able to customize the modules that are enabled, when dialog starts. One does this by altering the options in `config.properties`. For more details, see the [detailed documentation page](https://roboydialog.readthedocs.io/en/devel/user_manual/2_configuration.html#configuration

### Logging Levels

One can configure the amount of logging one gets from `memory`, `dialog` and `parser` individually. It is recommended that...

| User Type    | Dialog | Parser | Memory |
| ------------ | ------ | ------ | ------ |
| Developer    | DEBUG  | DEBUG  | FINE   |
| Regular User | INFO   | INFO   | INFO   |

If you wish to have more detailed information, please view the [docs page](https://roboydialog.readthedocs.io/en/devel/developer_manual/2_Logging_Policy.html).

### Enabling External APIs

If you want to set external APIs, you will have to create an `api.key` file containing all the keys. For more information, see the [relevant documentation page](https://roboydialog.readthedocs.io/en/devel/user_manual/2_configuration.html#enabling-external-apis)

## Troubleshooting

See the [Troubleshooting Page](https://roboydialog.readthedocs.io/en/devel/user_manual/9_Troubleshooting.html)