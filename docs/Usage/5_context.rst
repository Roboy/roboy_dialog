Context
=======

The goal of Context is to collect information about Roboy's environment and state. This information can be used by the DM classes and also to react upon situations that match certain conditions, such as turning the head of Roboy when the Interlocutor moves.

Architecture
------------

Graph to be added.

The Context supports storing various data as attributes. A value-based attribute only stores the latest DataType object that was pushed to it. A history-based attribute stores every value it receives and assigns each a unique key, thus the values can be ordered by their adding time.