************************
Documentation Guidelines
************************

If you want to extend this documentation, this page provides help on choosing where to extend it.

User manual
===========

.. NOTE::
	The user manual section is the place where everything a **user** of dialog system needs to read.

All information necessary for someone deploying the dialog system in order to have a humanlike interaction service goes here. This incorporates information or references to them on how to install (especially this information should be in the respective README and just be referenced in this documentation), configure and run the dialog system. Additionally this section includes short explanations and references to development-specific stuff typical users might need like how to build a personality for their usecase.

System architecture
===================

.. Note::
	The system architecture section is where developers go to understand the dialog system.

This section includes conceptual information about the dialog system while mentioning code where it fits in order to prepare the reader to explore the code while understanding the dialog system.

Developer manual
================

.. Note::
	The developer manual section is where developers go in order to learn about or look up specific 	implementation details while developing.

This section contains implementation specific details that need additional explanation and general information about the project structure (where to find what, etc.). 
If extending the dialog system in a specific place requires multiple steps all over the dialog system, please add a tutorial for this. (For example ``roboy.io.InputDevice`` and ``roboy.io.OutputDevice``)
