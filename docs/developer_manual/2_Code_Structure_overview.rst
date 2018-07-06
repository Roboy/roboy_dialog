.. _Code structure overview:

***********************
Code structure overview
***********************
This page gives overview of the project to help navigating the dialog system code.

The project structure
=====================

dialog
------
Contains everything specific to the Dialog System functionalities.

resources/
""""""""""
Resources needed by the dialog system only.

src
^^^

**integration-test**: Contains integration tests for combined testing of several Dialog System units.

**test**: Contains unit tests for testing isolated Dialog System units.

main
""""

**context**: Contains the *Context* and all classes only relevant in combination with *Context*.

**dialog**: Everything necessary to conduct the concept of a dialog: Personalities, Statemachine, Conversations, etc.

**emotions**: Contains information about possible emotions.

**io**: Contains Input- and OutputDevices.

**linguistics**: Contains NLU Wrappers + help classes for them and some very simple linguistic analyis.

**logic**: Contains deduction and inference.

**memory**: Contains memory wrappers and processing classes.

**ros**: Contains ROS wrappers and interfaces.

**talk**: Contains sentence-building logic.

**util**: Contains Dialog System wide utilities.

docs
----

Readthedocs documentation in reStructuredText format. The product of it's compilation is this documentation.

submodules
----------

The nlu and memory submodules are imported at a specific commit from other repositories. Please do not change anything but the checked out commit within the Dialog System repository. In order to change nlu/memory functionality, refer to their specific repositories.



Where to extend functionalities
===============================

Pick the corresponding interface, depending on which part of the system you want to extend. If you want to add new devices go for the input or output device interfaces. If you want to extend the linguistic analysis implement the Analyzer interface or extend the SentenceAnalyzer class. If you are happy with input, linguistics and output and just want to create more dialog, implement the Personality interface.
For more advanced functionality like *world interfaces* you'd need to implement different functionalities in differnt places. Refer the project structure above and the :ref:`Development tutorials` for this.

+--------------------+--------------------------------------------------+
| Create a new ...   | By implementing ...                              |
+====================+==================================================+
| Input Device       | de.roboy.io.InputDevice                          |
+--------------------+--------------------------------------------------+
| NLP Analyzer       | de.roboy.linguistics.sentenceanalysis.Analyzer   |
+--------------------+--------------------------------------------------+
| State Machine      | de.roboy.dialog.personality.Personality          |
+--------------------+--------------------------------------------------+
| State              | de.roboy.dialog.personality.states.State         |
+--------------------+--------------------------------------------------+
| Action type        | de.roboy.dialog.action.Action                    |
+--------------------+--------------------------------------------------+
| Output device      | de.roboy.io.OutputDevice                         |
+--------------------+--------------------------------------------------+

The interfaces are deliberately simple, containing only 0 - 2 methods that have to be implemented. Once you implemented your new classes include them in the personality used in ``de.roboy.dialog.ConversationManager``, if you only implemented single states or directly in ``de.roboy.dialog.ConversationManager`` for everything else.
