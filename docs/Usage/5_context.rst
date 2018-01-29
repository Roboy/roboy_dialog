*******
Context
*******

The goal of Context is to collect information about Roboy's environment and state. This information can be used by the DM classes and also to react upon situations that match certain conditions, such as turning the head of Roboy when the Interlocutor moves.

Architecture
============

.. figure:: ../images/context.png
:alt: Context architecture

The Context supports storing data as a ``Value`` or ``ValueHistory``. A ``Value`` only stores the latest data object that was pushed to it. A ``ValueHistory`` stores every value it receives and assigns each a unique key, thus the values can be ordered by their adding time.


How to add Values?
==================

Here we describe how a new Value can be created and added to the Context. Sample implementations can be found inside ``roboy.context.contextObjects`` package.

1. Consider what type of data will be stored in the Value. For this example, we chose ``String``.

2. In the ``contextObjects`` directory, create a new class which inherits from the Value class. The final signature should look similar to: ``public class SampleValue extends Value<String>`` (replacing String with your type).

3. Make the value available over the enum ``Values`` within the Context class by adding a new element with your class name and stored data type. For example: ``SAMPLE_VALUE(SampleValue.class, String.class);``

4. Congratulations, you can now query the new Value object! ...but it does not receive any values yet. To change this, see "How to add Updaters?" below.

How to add ValueHistories?
==========================

ValueHistories extend the functionality of Values by storing all data objects sent to them. Over the ``getNLastValues(int n)`` method, a map with several most recent data objects can be retrieved, including their ordering.

Adding a ``ValueHistory`` is very much alike to adding a ``Value``, just make sure to:

1. extend ``ValueHistory<>`` instead of ``Value<>``,

2. in ``Context.java``, add the new object to the enum ``ValueHistories`` instead of ``Values``.

How to add Updaters?
====================
a
New values can only flow into the Context over an ``Updater`` instance. Internal updaters can be used by DM classes to actively add new values. External updaters run in separate threads and seek out new values, for example over a ROS connection to the Vision module.

Adding an External Updater
""""""""""""""""""""""""""
Currently, the only implementation of an external updater is the ``IntervalUpdater`` abstract class. Usage:

1. Create a class extending ``IntervalUpdater`` and implement its ``update()`` method. It should retrieve the values and finally add them over the ``target.updateValue(value)`` method call.

2. A constructor is required for the class. Simply match the InternalUpdater constructor and call ``super(target)`` within - or use the two-parameter constructor to change the update frequency (by default 1 second). An example is in the ``FaceCoordinatesUpdater`` class.

3. Add the updater to the ``ExternalUpdaters`` enum inside ``Context.java``, setting its ``target`` parameter to be the ``Value`` or ``ValueHistory`` class it should update.

Adding a new Internal Updater
"""""""""""""""""""""""""""""
1. Create a class extending InternalUpdater<*targetClass*, *valueType*>. The class and data type of the target ``Value`` or ``ValueHistory`` are the generic parameters for the updater.

2. A constructor is required for the class. Simply match the InternalUpdater constructor and call ``super(target)`` within. An example is in the ``DialogTopicsUpdater`` class.

3. Add an entry to the ``InternalUpdaters`` enum similarly as discussed above.