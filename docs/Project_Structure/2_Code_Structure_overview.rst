***********************
Code structure overview
***********************

This page gives overview how the roboy_dialog project is structured.

dialog
======
Contains everything specific to the Dialog System functionalities.

resources/
""""""""""
Resources needed by the dialog system only.

src
---

**integration-test**: Contains integration tests for combined testing of several Dialog System units.
**test**: Contains unit tests for testing isolated Dialog System units.

main
^^^^

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
=====

Readthedocs documentation in reStructuredText format. The product of it's compilation is this documentation.

submodules
==========

The nlu and memory submodules are imported at a specific commit from other repositories. Please do not change anything but the checked out commit within the Dialog System repository. In order to change nlu/memory functionality, refer to their specific repositories.