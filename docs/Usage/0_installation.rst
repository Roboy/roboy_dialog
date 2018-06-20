Installation
=============

Requirements
------------------

- Apache Maven
- Java 8 (Oracle is prefered)
- Git
- Word2Vec File (for Parser) TODO: Add links to relevant Parser Doc
- Neo4J Todo: Add link to relevant memory section


Recommendations
------------------

- Intellij
- At least 8GB of RAM
- Ubuntu (or variation) 16.04 LTS [1]
    - Needed for ``ROS Kinetic``


Using command line
------------------

Install Maven on the computer.
``sudo apt install maven``

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog -b devel --recursive``

Navigate to the root module.
``cd roboy_dialog``

Compile the project - Maven will take care of the rest.
``mvn compile`` Todo: Change this to mvn clean install?

Execute the project.
``mvn exec:java -Dexec.mainClass="roboy.dialog.DialogSystem"`` TODO: This command is changed, what is it supposed to be?


Using IDE (Eclipse, Intellij IDEA)
----------------------------------

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog``

Now, import Dialog System as a Maven project into the IDE of your choice. Build and execute using ``roboy.dialog.DialogSystem`` as the main class.


[1] Note that ``apt`` is partially broken with Xubuntu 16.04 and that you will require to deinstall _____ in order to get ``apt-get update`` to work