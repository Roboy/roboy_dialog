

Dialog System Scope
===================

The Roboy Dialog System talks to and logically embeds the Roboy Semantic Parser and the Roboy Memory Module.
The Memory Module is called by Roboy Dialog to and takes the a `JSON String <http://roboy-memory.readthedocs.io/en/latest/ScopeContext/3_public_interfaces.html>`_as a its parameter.

The Semantic Parser receives input in form of an Interpretation via a websocket.

The main input of the Dialog System is either a command line string or a speech-to-text processed string.
The main output of the Dialog System is either a command line string or piece of audio data.

For further info refer to :ref:`in_out`.

The scope of the Roboy Dialog System is illustrated in the following diagram:

.. _context_within_environment:
.. figure:: images/uml_system_context.*
  :alt: Scope overview