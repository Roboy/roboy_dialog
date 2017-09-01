Installation
=============

We use Apache Maven build automation tool.

Using command line
------------------

Install Maven on the computer.
``sudo apt install maven``

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/DialogSystem``

Navigate to the root module.
``cd DialogSystem``

Compile the project - Maven will take care of the rest.
``mvn compile``

Execute the project.
``mvn exec:java -Dexec.mainClass="roboy.dialog.DialogSystem"``


Using IDE (Eclipse, Intellij IDEA)
----------------------------------

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/DialogSystem``

Now, import Dialog System as a Maven project into the IDE of your choice. Build and execute using ``roboy.dialog.DialogSystem`` as the main class.

