Configuration
=============

The Dialog Manager can be called with a specific system configuration that determines which external services will be used within the session. The ``ROS_HOSTNAME`` is set through the ``config.properties`` file at project root.

Usage
-----

Set profile in the execution invocation like this:
 ``mvn exec:java -Dexec.mainClass="roboy.dialog.DialogSystem" -Dprofile=NOROS``
If running from within an IDE, edit the run configurations to include the profile as VM option:
 ``-Dprofile=NOROS``

Without a specified profile, ``DEFAULT`` will be used. Please note that this profile requires setting a valid ``ROS_HOSTNAME`` address in the ``config.properties`` file to function properly! If ROS is not set up, use the ``NOROS`` profile to prevent the Dialog Manager from using ROS-dependent services.

Profiles
--------

+--------------------+-----------------------------------------------------------------+
|Profile             | Description                                                     |
+====================+=================================================================+
| DEFAULT            | Used when no other profile is set, assumes that all             |
|                    | requirements (ROS, Internet connection, speakers, mic) are      |
|                    | fulfilled.                                                      |
+--------------------+-----------------------------------------------------------------+
| NOROS              | To be used when ROS services are not set up, avoids calls to    |
|                    | memory, speech synthesis, voice output, etc.                    |
+--------------------+-----------------------------------------------------------------+
| STANDALONE         | To be used when running without Internet connection - this      |
|                    | profile includes all restrictions of NOROS and also does not    |
|                    | call DBPedia.                                                   |
+--------------------+-----------------------------------------------------------------+

Extending
---------

To extend or change the configurations, have a look at the instructions in the ``roboy.dialog.Config`` class.