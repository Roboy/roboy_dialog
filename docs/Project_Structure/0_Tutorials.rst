*********************
Development tutorials
*********************

This page is a collection of useful tutorials if you want to develop or enhance parts of the Dialog System.

=================================
Adding and Input- or OutputDevice
=================================
In order to add new ``roboy.io.InputDevice`` and ``roboy.io.OutputDevice`` classes, changes in multiple locations are necessary.
1. Implement your ``InputDevice`` or ``OutputDevice`` implementation using ``class [YOUR CLASSNAME] extends InputDevice`` (or OutputDevice, if you're doing output).
2. If your device needs additional cleaning in order to be destroyed properly, additionally use ``implements CleanUp`` and implement the ``cleanup()`` method.
3. Add your devices to ``roboy.util.io`` in ``getInputs()`` and ``getOutputs()``, so the dialog system may use them if they're chosen in the configuration.
4. Add a (commented) input/output configuration to ``config.properties``.