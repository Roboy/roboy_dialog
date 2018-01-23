*******
Context
*******

The goal of Context is to collect information about Roboy's environment and state. This information can be used by the DM classes and also to react upon situations that match certain conditions, such as turning the head of Roboy when the Interlocutor moves.

Architecture
============

.. figure:: images/overview_diagram.jpg
    :alt: Overview diagram

The Context supports storing data as a ``Value`` or ``ValueHistory``. A ``Value`` only stores the latest data object that was pushed to it. A ``ValueHistory`` stores every value it receives and assigns each a unique key, thus the values can be ordered by their adding time.


How to add Values?
==================

Here we describe how a new Value can be created and added to the Context. Sample implementations can be found inside ``roboy.context.contextObjects`` packge.

1. Consider what type of data will be stored in the Value. For this example, we chose ``String``.
2. In the ``contextObjects`` directory, create a new class which inherits from the Value class. The final signature should look similar to: ``public class SampleValue extends Value<String>`` (replacing String with your type).
3. Add the new Value in the main class ``Context.java``:
    1. In the standard constructor, initialize the Value object and add it to the builder of the value map: ``put(SampleValue.class, sampleValue)``
    2. Make the value available over the enum ``Values`` within the Context class by adding a new element with your class name and stored data type. For example: ``SAMPLE_VALUE(SampleValue.class, String.class);``

How to add ValueHistories?
==========================

ValueHistories extend the functionality of Values by storing all data objects sent to them. Over the ``getNLastValues(int n)`` method, a map with several most recent data objects can be retrieved, including their ordering.

Adding a ``ValueHistory`` is very much alike to adding a ``Value``, just make sure to:

1. extend ``ValueHistory<>`` instead of ``Value<>``,

2. in ``Context.java``, add the new object to the Builder of ``valueHistories`` instead of ``values``, and to the enum ``ValueHistories`` instead of ``Values``.

How to add Updaters?
====================

New values can only be added to the Context over an ``Updater`` instance. Internal updaters can be used by DM classes to actively add new values. External updaters run in separate threads and seek out new values, for example over a ROS connections to the Vision module.

Adding an External Updater
""""""""""""""""""""""""""
Currently, the only implementation of an external updater is the ``IntervalUpdater`` abstract class. Usage:

1. Create a class extending ``IntervalUpdater`` and implement its ``update()`` method. It should retrieve the values and finally add them over the ``target.updateValue(value)`` method call.
2. Add the updater to ``externalUpdaters`` in the ``Context.java`` constructor, setting its ``target`` parameter with the ``Value`` or ``ValueHistory`` object created in the same constructor.

Adding a new Internal Updater
"""""""""""""""""""""""""""""
1. Create a class extending InternalUpdater<*targetClass*, *valueType*>. The class and data type of the target ``Value`` or ``ValueHistory`` are the generic parameters for the updater.
2. A constructor is required for the class, simply match the InternalUpdater constructor and call ``super(target)`` within. An example is in the ``DialogTopicsUpdater`` class.
3. In the ``Context`` class constructor, initialize the updater and add it to the ``internalUpdaters`` map.
4. Add an entry to the ``Updaters`` enum, similarly as discussed above for ``Values``.