Installation
=============

Requirements
------------------

- Apache Maven
- Java 8 (Oracle is prefered)
- Git
- `Neo4J <http://roboy-memory.readthedocs.io/en/latest/Usage/0_installation.html#local-neo4j-instance>`_
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
``git clone https://github.com/Roboy/roboy_dialog -b master --recursive``

.. tip:: If you wish to clone another branch, e.g. devel, just simply replace ``master`` with the branch's name.

Set `Environmental Variables <http://roboy-memory.readthedocs.io/en/latest/Usage/1_getting_started.html>`_ (if not done so already). Your ``.bashrc`` should look like:

.. code-block:: bash

    export ROS_MASTER_URI="http://127.0.0.1:11311"
    export ROS_HOSTNAME="127.0.0.1"
    export NEO4J_ADDRESS="bolt://127.0.0.1:7687"
    export NEO4J_USERNAME="neo4j"
    export NEO4J_PASSWORD="neo4jpassword"
    export REDIS_URI="redis://localhost:6379/0"
    export REDIS_PASSWORD="root"

Navigate to the root module.
``cd roboy_dialog``

Download roboy parser.
``git submodule update --init ./roboy_parser``

Install roboy parser as described in the roboy_parser docs or the 'semantic parser' page in 'INTERFACES AND SCOPE'.

Compile the project - Maven will take care of the rest.
``mvn compile``

Execute the project.
``mvn exec:java -Dexec.mainClass="roboy.dialog.ConversationManager"``

Using IDE (Eclipse, Intellij IDEA)
----------------------------------

Clone the Dialog Manager repository either using your IDE's VCS Tools or using the command line.
``git clone https://github.com/Roboy/roboy_dialog``

.. attention:: Make sure the submodules are initialized. 

Now, import Dialog System as a Maven project into the IDE of your choice. Build and execute using ``roboy.dialog.ConversationManager`` as the main class.

Troubleshooting
------------------------------

See the `Troubleshooting Page <http://roboy-dialog.readthedocs.io/en/latest/Usage/9_troubleshooting.html>`_ 