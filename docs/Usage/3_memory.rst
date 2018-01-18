The memory module
=================

General design
--------------

To remember information about itself and its conversation partners, their hobbies, occupations and origin, a persistent Memory module has been implemented using the Neo4j graph database.

Implementation
--------------

Roboy's Dialog System interactions with the Memory module are based on ROS messages. The messages are sent using the methods in ``de.roboy.ros.RosMainNode``, which implements the four query types based on the specified Memory services:

+--------------------+--------------------------------------------------+
| Method name        | Description                                      |
+====================+==================================================+
| CreateMemoryQuery  | Creates a node in Memory database                |
+--------------------+--------------------------------------------------+
| UpdateMemoryQuery  | Adds or changes information of an existing node  |
+--------------------+--------------------------------------------------+
| GetMemoryQuery     | Retrieves either one node or an array of IDs     |
+--------------------+--------------------------------------------------+
| DeleteMemoryQuery  | Removes information from or deletes a node       |
+--------------------+--------------------------------------------------+
| CypherMemoryQuery  | For more complex queries (future)                |
+--------------------+--------------------------------------------------+

The messages received from Memory are in JSON format. To enable flexible high-level handling of Memory information, two classes were created to incorporate the node structures and logic inside the Dialog System. The ``de.roboy.memory.nodes.MemoryNodeModel`` contains the labels, properties and relationships in a format which can be directly parsed from and into JSON. For this, Dialog is using the GSON parsing methods which enable direct translation of a JSON String into its respective Java class representation.

Methods such as ``getRelation()`` or ``setProperties()`` were implemented to allow intuitive handling of the MemoryNodeModel instances. A separate class, ``de.roboy.memory.nodes.Interlocutor``, encapsulates a MemoryNodeModel and is intended to further ease saving information about the current conversation partner of Roboy. Interlocutor goes one step further by also abstracting the actual calls to memory, such that adding the name of the conversant performs an automatic lookup in the memory with subsequent updating of the person-related information. This is then available in all subsequent interactions, such that Roboy can refrain from asking questions twice, or refer to information he rememberes from earlier conversations.
