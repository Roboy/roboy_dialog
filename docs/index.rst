.. Software Documentation template master file, created by
   sphinx-quickstart on Fri Jul 29 19:44:29 2016.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Roboy Dialog System
===========================================================

This project aims to implement human-like conversation routines for the humanoid anthropomimetic robot Roboy. The overview diagram shows the external systems which Dialog System interacts with, and the tasks for which the system is responsible.

.. figure:: images/overview_diagram.jpg
    :alt: Overview diagram

Status
------

Stable functionality:

- Roboy introduces himself
- Roboy answers questions about himself
- Roboy recognizes once someone says his name
- Roboy asks questions people he meets

In development:

- Roboy stores and recalls information (name, occupation, ect.) about people he meets
- Roboy recognizes the intent behind an asked questions (age, creator, capabilities etc.)


Contents:
----------

.. _usage:
.. toctree::
  :maxdepth: 1
  :glob:
  :caption: Usage and Installation

  Usage/*

.. _ScopeContext:
.. toctree::
  :maxdepth: 1
  :glob:
  :caption: Interfaces and Scope

  ScopeContext/*
  
.. _Architecture:
.. toctree::
   :maxdepth: 1
   :glob:
   :caption: Architecture
   
   Architecture/*


.. toctree::
   :maxdepth: 1

   about-arc42
