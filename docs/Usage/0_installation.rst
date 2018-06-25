Installation
=============

Requirements
------------------

- Apache Maven
- Java 8 (Oracle is prefered)
- Git
- Neo4J Todo: Add link to relevant memory section
- Working Internet Connection for downloading dependencies


Recommendations
------------------

- Intellij
- At least 8GB of RAM
- Ubuntu (or variation) 16.04 LTS [1]
    - Needed for ``ROS Kinetic``


.. note:: Note that ``apt`` is bugged with Ubuntu 16.04. See: https://bugs.launchpad.net/ubuntu/+source/appstream/+bug/1583845 if you get problems with ``apt-update``.

Using command line
------------------

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog -b devel --recursive``
>Note: Replace devel with the branch you desire. 

Set Environmental Variables (if not done so already)
See Memory Documentation for more Information

Navigate to the root module.
``cd roboy_dialog``

Clean and then Install the project using Maven
``mvn clean install``

Execute the project.
``java -Xmx6g -d64 -cp dialog/target/ roboy-dialog-system-2.1.9-jar-with-dependencies.jar roboy.dialog.DialogSystem``

Using IDE (Eclipse, Intellij IDEA)
----------------------------------

Clone the Dialog Manager repository.
``git clone https://github.com/Roboy/roboy_dialog``

Important: Make sure the submodules are initialized. 

Now, import Dialog System as a Maven project into the IDE of your choice. Build and execute using ``roboy.dialog.DialogSystem`` as the main class.

Troubleshooting
------------------------------

See the Troubleshooting Page