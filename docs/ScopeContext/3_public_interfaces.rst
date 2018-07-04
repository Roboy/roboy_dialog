.. _technical-interfaces:

Public Interfaces
==================

Interfaces to other (sub)modules are realized through direct function calls. The original, now deprecated, ROS interfaces are still present and may be used, if required.

ROS
-------------

The memory, vision, emotion, speech, generative model and middleware communication is carried out through RosMainNode object which implements AbstractNodeMain
(inheriting NodeListener of rosjava) and offering the control through the following important methods:

    - onStart
    - onShutdown
    - onShutdownComplete
    - onError

Currently, it also provides with the next custom methods:

    - SynthesizeSpeech
    - RecognizeSpeech
    - GenerateAnswer
    - ShowEmotion
    - CreateMemoryQuery
    - UpdateMemoryQuery
    - GetMemoryQuery
    - DeleteMemoryQuery
    - CypherMemoryQuery
    - DetectIntent
    - addListener
    - waitForLatchUnlock


