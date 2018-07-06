.. _technical-interfaces:

Public ROS Interfaces
===============================

Interfaces to other (sub)modules are realized through direct function calls, as opposed to ROS calls. Despite this, some features, such as those listed below, still require ROS to communicate.

ROS
-------------

The vision, emotion, speech, generative model and middleware communication is carried out through RosMainNode object which implements AbstractNodeMain (inheriting NodeListener of rosjava) and offering the control through the following important methods:

    - onStart
    - onShutdown
    - onShutdownComplete
    - onError

Currently, it also provides with the next custom methods:

    - SynthesizeSpeech
    - RecognizeSpeech
    - GenerateAnswer
    - ShowEmotion
    - [Deprecated] CreateMemoryQuery
    - [Deprecated] UpdateMemoryQuery
    - [Deprecated] GetMemoryQuery
    - [Deprecated] DeleteMemoryQuery
    - [Deprecated] CypherMemoryQuery
    - DetectIntent
    - addListener
    - waitForLatchUnlock