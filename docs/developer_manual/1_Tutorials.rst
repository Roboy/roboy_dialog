.. _Development tutorials:

*********************
Development tutorials
*********************

This page is a collection of useful tutorials if you want to develop or enhance parts of the Dialog System.

Changing the dialog systems behaviour during a conversation
===========================================================

.. highlight:: java


.. _tut_new_state:

Adding a New State
------------------

Roboy often visits different events and you might want him to say something specific, for example mention a company or a sponsor. One way to do this would be to modify an existing state. However, these changes are often discarded as you still want to have the old behaviour. There is a better way: create a new custom state specifically for your needs.

In this tutorial you will learn how to design and implement a new state. To keep everything simple, the state will just ask a yes-no question and listen to the answer. Based on the answer, you will pick one of two replies and choose one of two transitions.


Do you know math?
^^^^^^^^^^^^^^^^^

Let's start! We are going to add a state that tests whether the interlocutor (person speaking to Roboy) knows some basic math. First, create a new class named ``DoYouKnowMathState`` that extends from ``roboy.dialog.states.definitions.State``::

    // inside DoYouKnowMathState.java

    public class DoYouKnowMathState extends State {

    }

Your IDE will notify you that three functions (``act()``, ``react(...)`` and ``getNextState()``) have to be implemented. Let's add them::

    // inside DoYouKnowMathState.java

    @Override
    public Output act() {
        return null;
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        return null;
    }

Additionally, we need a special constructor and a new variable to store the next state for later::

    // inside DoYouKnowMathState.java

    private State next;

    public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

Now, we can write some logic and define what our new state should do. The ``act()`` function is always executed first. In our case, we want to ask a simple question. Replace ``return null;`` inside ``act()`` with following::

    // inside public Output act()

    return Output.say("What is 2 plus 2?");

The interlocutor's answer will be passed to the ``react(...)`` function once it is available. Inside, we should check whether the answer is correct and react based on correctness. This code is one of the simplest ways to do this::

    // inside public Output react(Interpretation input)

    // get tokens (= single words of the input)
    String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

    // check if the answer is correct (simplest version)
    if (tokens.length > 0 && tokens[0].equals("four")) {
        // answer correct
        next = getTransition("personKnowsMath");
        return Output.say("You are good at math!");
    } else {
        // answer incorrect
        next = getTransition("personDoesNotKnowMath");
        return Output.say("Well, 2 plus 2 is 4!");
    }

Note a few things here:

- to keep this tutorial simple, we only check whether the first word of the reply equals "four"
- based on reply correctness, we get the next state using ``getTransition(<transitionName>)`` save it for later
- similarly to ``act()``, we define the output with ``return Output.say(<stringToSay>);``

Finally, we can implement the last required function ``getNextState()`` that defines the next state to enter. Inside, we just return the next state that we defined inside ``react(...)``::

    // inside public State getNextState()

    return next;

That's it, you have just created your first state! Here is how the class should look like::

    // inside DoYouKnowMathState.java

    package roboy.dialog.tutorials.tutorialStates;

    import roboy.dialog.states.definitions.State;
    import roboy.dialog.states.definitions.StateParameters;
    import roboy.linguistics.Linguistics;
    import roboy.linguistics.sentenceanalysis.Interpretation;

    public class DoYouKnowMathState extends State {

        private State next;

        public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
            super(stateIdentifier, params);
        }

        @Override
        public Output act() {
            return Output.say("What is 2 plus 2?");
        }

        @Override
        public Output react(Interpretation input) {

            // get tokens (= single words of the input)
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

            // check if the answer is correct (simplest version)
            if (tokens.length > 0 && tokens[0].equals("four")) {
                // answer correct
                next = getTransition("personKnowsMath");
                return Output.say("You are good at math!");

            } else {
                // answer incorrect
                next = getTransition("personDoesNotKnowMath");
                return Output.say("Well, 2 plus 2 is 4!");
            }
        }

        @Override
        public State getNextState() {
            return next;
        }
    }


The newest version of the complete code can be found in in ``roboy.dialog.tutorials.tutorialStates.DoYouKnowMathState``. Read the :ref:`tut_new_personality` tutorial to learn how to connect your new state with others.


Example output
^^^^^^^^^^^^^^

When using the new state, you could encounter the conversation::

    [Roboy]: What is 2 plus 2?
    [You]:   four
    [Roboy]: You are good at math!

Or, if you provide a wrong answer::

    [Roboy]: What is 2 plus 2?
    [You]:   one
    [Roboy]: Well, 2 plus 2 is 4!

To learn more details about states and personalities, refer to :ref:`personality_and_states`. There, you will find details about state fallbacks, parameters and interfaces, as well as more information about different personalities and more output options.




.. highlight:: json

.. _tut_new_personality:

Creating a New Personality
--------------------------

Roboy's Dialog System can be used in different environments and situations like fairs, conferences, demos or as a chatbot on social networks. For every given situation, Roboy's behaviour should be different. We use personalities to define Roboy's way of talking.

In this tutorial you will learn how to create a new personality. Make sure that you know the basic functionality of states. If you are not familiar with them, read the :ref:`tut_new_state` tutorial. General information about personalities can be found on :ref:`personality_and_states`.

Personalities are defined inside JSON personality files. Each file represents a state machine and defines:

- initial state: state in which Roboy starts the conversation
- transitions: connections between the states and the dialog flow
- fallbacks: backup if a state fails to react to unexpected input

State definition
^^^^^^^^^^^^^^^^

Every state inside the personality file is defined by a JSON object. Here is an example::

    {
      "identifier": "MathTest",
      "implementation" : "roboy.dialog.tutorials.tutorialStates.DoYouKnowMathState",
      "transitions" : {
        "personKnowsMath" : "Farewell",
        "personDoesNotKnowMath" : "Farewell"
      },
      "comment": "A state that will test your math knowledge."
    }

We have just defined a state that is called ``MathTest``. Every state must have a unique identifier.

The ``implementation`` property defines which Java class should be used for this state when the Dialog System is running. When the Dialog System loads a personality file, it creates a Java object of the right class for *every* state defined in the file.

It is important to provide the complete class name (including the package) so that the Dialog System can find the right class and instantiate an object of it when loading the file. Special care is needed when doing refactoring. Make sure to change the personality file when you rename a state or move it to a different package!

Next, we have ``transitions``. Here we define the connections between states. You should define all transitions that could be taken by the state implementation. For the ``DoYouKnowMathState`` we have two of them: ``personKnowsMath`` and ``personDoesNotKnowMath`` (look for ``getTransition(<transitionName>)`` inside the Java code). In the JSON file, the key is the transition name (e.g. ``personKnowsMath``) and the value (here ``Farewell``) is the identifier of another state in the *same* personality file (do not confuse with Java class names). We will take a look at the definition of the ``Farewell`` state a bit later.

The ``comment`` property is optional and will be ignored completely by the Dialog System. It just gives you an option to note some details about the state. There are two additional properties that you can (and sometimes have to) define: ``parameters`` and ``fallback``. We will discuss them later as well.

Now, let's define the ``Farewell`` state. We will use one of the pre-implemented toy states. The definition looks like this::

    {
      "identifier": "Farewell",
      "implementation" : "roboy.dialog.tutorials.tutorialStates.ToyFarewellState",
      "transitions" : {},
      "comment": "Last state: Tells goodbye, ignores reply, ends the conversation."
    }

Nothing new here, except that we have no outgoing transitions for this state. This is because the ``ToyFarewellState`` always ends the conversation and will never take any transition.


Complete personality file
^^^^^^^^^^^^^^^^^^^^^^^^^

With two states defined, we can now take a look at the complete personality file. All state definitions are stored in the ``states`` array. Additionally, we define the ``initialState`` and pass the identifier ``MathTest`` of our ``DoYouKnowMathState``. The complete file looks like this::

    {
      "comment": "A simple personality that only contains two states (used in tutorial).",
      "initialState": "MathTest",
      "states": [
        {
          "identifier": "MathTest",
          "implementation" : "roboy.dialog.tutorials.tutorialStates.DoYouKnowMathState",
          "transitions" : {
            "personKnowsMath" : "Farewell",
            "personDoesNotKnowMath" : "Farewell"
          },
          "comment": "A state that will test your math knowledge."
        },
        {
          "identifier": "Farewell",
          "implementation" : "roboy.dialog.tutorials.tutorialStates.ToyFarewellState",
          "transitions" : {},
          "comment": "Last state: Tells goodbye, ignores reply, ends the conversation."
        }
      ]
    }

This file is stored under ``resources/personalityFiles/tutorial/MathTest.json``. You can try running this personality by setting the path (``PERSONALITY_FILE``) in the config file (``config.properties``).

When you create a new personality file you might forget to define important transitions. To find errors faster, you can define the state interface (required transitions, parameters and fallback) for every state when you implement it. While loading the personality file, the Dialog System will check whether the state has everything it needs and warn you if something is missing. Read more about state interfaces on :ref:`personality_and_states`.


Fallbacks and parameters
^^^^^^^^^^^^^^^^^^^^^^^^

There are two additional properties that you can add to a state definition: ``parameters`` and ``fallback``. Take a look at an example::


    {
      "identifier": "Intro",
      "implementation": "roboy.dialog.tutorials.tutorialStates.ToyIntroState",
      "transitions": {
        "next": "Farewell"
      },
      "fallback": "RandomAnswer"
      "parameters" : {
        "introductionSentence" : "My name is Roboy!"
      }
    }

Let's take a look at both properties. Here we define ``RandomAnswer`` (which is an identifier of another state in the same personality file) as the fallback for the state with identifier ``Intro``. This means that if ``Intro`` cannot react to an input, the ``RandomAnswer`` will be asked instead. The property ``parameters`` allows you to pass parameters to the state. Each parameter has a name (here ``introductionSentence``) and a string value. The state implementation can access the value by the name. Parameters are very useful to pass resource file paths to states. Read more about fallbacks and parameters on :ref:`personality_and_states`.



Larger personality
^^^^^^^^^^^^^^^^^^

It is not easy to create interesting conversations using only two states (assuming relatively simple states of course). Usually, you will use at least five different states in one conversation. To get some experience in writing personality files, let's create a file that uses four states. Don't worry, you don't have to implement the states here. We will use four already pre-implemented toy states that can be found in the ``roboy.dialog.tutorials.tutorialStates`` package. The final personality should look like this:

.. figure:: images/toy_personality.png
    :alt: Toy personality

As you can see, we have four states that are connected to each other. The names of the transitions are denoted on the arrows. Now, try to write a personality file to represent this personality. Following these steps might be helpful:

- read the JavaDoc of every state you will use (``ToyGreetingsState``, ``ToyIntroState``, ``ToyFarewellState`` and ``ToyRandomAnswerState``)
- create a new personality file (you might copy ``MathTest.json`` to have an easier start)
- create four state definitions with different identifiers (``Greetings``, ``Intro``, ``Farewell`` and ``RandomAnswer``)
- define the initial state of your personality (``Greetings``)
- define the transitions between the states (note that fallback is not a transition)
- define the fallback for the ``Greetings`` state
- define required parameters for the ``Intro`` state (read JavaDoc of ``ToyIntroState`` for details)
- save the file in the ``resources/peronalityFiles`` folder
- edit the ``config.properties`` file and change ``PERSONALITY_FILE`` to your path
- try running the Dialog System

If anything goes wrong, you can always take a look at the solution saved in ``resources/peronalityFiles/tutorial/ToyStateMachine.json``. Happy personalizing!


Why do we need this complexity?
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You might be wondering why such a complex system with all those JSON files is needed. It would be much simpler to define all the states and transitions directly from core, right? Defining everything from code would indeed simplify the refactoring. However, definitions inside personality files have some essential advantages. First, you don't have to recompile the project just to change a personality. Second, in the future, we plan to implement a graphical editor for personalities and therefore need a file format to store the personalities. Using the editor, you will be able to create your own personality with drag & drop and don't have to worry about writing the personality files manually anymore.

While the editor is not implemented yet, we still have good news for you. You *can* define personalities directly from code and don't have to worry about creating a personality file (and updating it while refactoring). This feature is especially useful when writing unit tests for single states or smaller state machines. This tutorial does not cover creating personalities from code but there are good examples in the ``roboy.dialog.tutorials.StateMachineExamples`` class. Take a look at it if you need to define personalities from code.

Adding New Questions to the State
---------------------------------

There exists a list of questions, we may want Roboy to ask in order to acquire new information about people and the environment.
It is stored in the resources directory under sentences/QAList.json and follows the next JSON structure as given:

    "FRIEND_OF": {
        "Q": [
          "Who is your best friend?",
          "Have I met any of your friends?",
          "Do you have a friend whom I have met?",
          "Maybe I know some friends of yours. Would you name one?"
        ],
        "A": {
          "SUCCESS": [
            "Oh, I believe I have met %s they're nice."
          ],
          "FAILURE": [
            "I don't think I know them."
          ]
        },
        "FUP": {
          "Q": [
            "Have you made any new friends, %s?"
          ],
          "A": [
            "Oh, I have met %s they're nice."
          ]
        }
     }

Here, we have a set of questions about friends ("FRIEND_OF" intent), so Roboy can learn about friends of the person he is talking to. "SUCCESS" and "FAILURE" are the answerS, Roboy will say
if the information input was processed successfully or not, respectively.
Follow up questions ("FUP") are the ones that are used to update the information in the future if the questions ("Q") were already asked.

We can add a new entry there with a new intent. Let it be "LIKE":

    "LIKE": {
        "Q": [
          "What do you like?"
        ],
        "A": {
          "SUCCESS": [
            "Me too. I really like %s!"
          ],
          "FAILURE": [
            "Well, I do not know what to think about this"
          ]
        },
        "FUP": {
          "Q": [
            "Do you still like, %s?"
          ],
          "A": [
            "Maybe, I should consider liking this stuff"
          ]
        }
    }

.. highlight:: java

Then we have to add a new entry into our local ontology - Neo4jRelationships::

    public enum Neo4jRelationships {
        ...
        LIKE("LIKE");

        ...
    }

Go back to your state and inside the act() method implement the following logic::

    Interlocutor person = getContext().ACTIVE_INTERLOCUTOR.getValue();

    RandomList<String> questions = qaValues.getQuestions(Neo4jRelationships.LIKE);
    String question = questions.getRandomElement();
    return State.Output.say(question);

Now, we can ask these newly added questions and later process the answers in the react() method.

Querying the Memory from the Dialog System
------------------------------------------


Indeed, the newly created state may be the pinnacle of State Machines practice, but it does not yet exploit all of the Roboy Dialog System capabilities, such as
the graph database Roboy Memory Module which allows to store and recall information about the environment. For instance, you may want to check whether you belong to
the circle of Roboy's friends.

Every state is bundled with the memory reference inside its parameters, to call the memory you have to access it the following way::

    Neo4jMemoryInterface memory = getParameters().getMemory();

Then you may want to call one of the most used methods, namely, getById - which will query the Neo4j database and get the description of the node with the same (unique) ID
in JSON format. Roboy's ID is 26.::

    String requestedObject = getMemory().getById(26);
    MemoryNodeModel roboy = gson.fromJson(requestedObject, MemoryNodeModel.class);

The MemoryNodeModel class is the general class which is a model for the nodes stored in Neo4j. It has a label, an ID, parameters and relationships with other nodes denoted by IDs.
As soon as we have the Roboy node we can get his friends' IDs like this:

    ArrayList<Integer> ids = roboy.getRelationships(Neo4jRelationships.FRIEND_OF);

Then we can proceed with checking Roboy's friends as follows::

    RandomList<MemoryNodeModel> roboyFriends = new RandomList<>();

    if (ids != null && !ids.isEmpty()) {
        try {
            Gson gson = new Gson();
            for (Integer id : ids) {
                String requestedObject = getParameters().getMemory().getById(id);
                roboyFriends.add(gson.fromJson(requestedObject, MemoryNodeModel.class));
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Error on Memory data retrieval: " + e.getMessage());
        }
    }

Let's check if we are friends with him::

    if (!roboyFriends.isEmpty()) {
        for (MemoryNodeModel friend : roboyFriends) {
            if (friend.getProperties().get("name").toString() == myName) {
                success = true;
                break;
            }
        }
    }

However, there exists a special Roboy node class initialized in a specific way like this::

    Roboy roboy = new Roboy(memory);

It will retrieve and fill all the data for Roboy from the memory.

Furthermore, we wanted to make it less of miserable routine thus there is a helper function in the State superclass, which makes your life much easier::

    RandomList<MemoryNodeModel> nodes = retrieveNodesFromMemoryByIds(roboy.getRelationships(Neo4jRelationships.FRIEND_OF));

    if (!nodes.isEmpty()) {
        for (MemoryNodeModel node : nodes) {
            if (node.getProperties().get("name").toString() == myName) {
                success = true;
                break;
            }
        }
    }

Creating a Value History / Storing and Updating Values in the Context
---------------------------------------------------------------------

See :ref:`context`


Extending the Lexicon and the Grammar
-------------------------------------

This tutorial explains how to create or change grammar and lexicon used in the semantic parser.

Lexicon
"""""""

To create your own custom lexicon, you need to create a new file or copy an existing lexicon and add lexemes in the following format::

    {lexeme:"LEXEME", formula:"FORMULA", type:"TYPE"}

where:

- lexeme - is a natural language utterance, e.g., name

- formula - is a semantic representation of the lexeme, e.g., rb:HAS_NAME

- type - is a lexeme type, e.g., NamedEntity, fb:type.any

Additionally, you can also add features in JSON format for map::

    {lexeme:"name", formula:"rb:HAS_NAME", type:"DataProperty", features:"{feature1:0.5, feature2:0.3}"}

Grammar
"""""""

To create your own custom grammar, you need to create a new file or copy existing grammar and add rules in the following format::

    (rule [Category] ([Expression]) ([Function]))

where:

- Category - is a category of rule, for root derivation use $ROOT

- Expression - is a format of text accepted by the rule expressed in your custom categories or names, e.g., $PHRASE, $TOKEN, $Expr

- Function - semantic function that should be applied to specified pattern, e.g., IdentityFn

Example rules::

    (rule $ROOT ((what optional) (is optional) $Expr (? optional)) (IdentityFn))
    (rule $Expr ($Expr $Conversion) (JoinFn backward))

For in-depth tutorial on expression and function types, refer to original SEMPRE `tutorial <https://github.com/percyliang/sempre/blob/master/TUTORIAL.md>`_ or `documentation <https://github.com/percyliang/sempre/blob/master/DOCUMENTATION.md>`_

Used files in configuration
"""""""""""""""""""""""""""

To use created files, you need to set the correct parameter in ``pom.xml`` file.
For grammar::

    -Grammar.inPaths

For lexicon::

    -SimpleLexicon.inPaths


Scoring Functions and Knowledge Retrieval
-----------------------------------------

Currently, our semantic parser uses error retrieval mechanism that can be modified in the following steps:

Scoring Function
""""""""""""""""

1. Move to package::

    edu.stanford.nlp.sempre.roboy.score

2. Implement ``edu.stanford.nlp.sempre.roboy.score.ScoringFunction`` class with score method.

3. Add scoring function in constructor of ``edu.stanford.nlp.sempre.roboy.ErrorRetrieval`` class.

Knowledge Retriever
"""""""""""""""""""

1. Move to package::

    edu.stanford.nlp.sempre.roboy.error

2. Implement ``edu.stanford.nlp.sempre.roboy.error.KnowledgeRetriever`` class with analyze method.

3. Add knowledge retriever in constructor of ``edu.stanford.nlp.sempre.roboy.ErrorRetrieval`` class.

.. _Using the Context:

Using the Context
=================


How to add Values?
------------------

Here we describe how a new Value can be created and added to the Context. Sample implementations can be found inside ``roboy.context.contextObjects`` package.

1. Consider what type of data will be stored in the Value. For this example, we chose ``String``.

2. In the ``contextObjects`` directory, create a new class which inherits from the Value class. The final signature should look similar to: ``public class SampleValue extends Value<String>`` (replacing String with your type).

3. Make the value available for the Dialog System by defining a ``ValueInterface`` in the ``Context.java`` class, among other class variables. A ``ValueInterface`` takes two type parameters: the ``Value`` class created in step 2, and its data type (in our case, ``String``). Example: ``public final ValueInterface<SampleValue, String> SAMPLE_VALUE = new ValueInterface<>(new SampleValue());``

4. Congratulations, you can now query the new Value object! ...but it does not receive any values yet. To change this, see "How to add Updaters?" below.

How to add ValueHistories?
--------------------------

ValueHistories extend the functionality of Values by storing all data objects sent to them. Over the ``getNLastValues(int n)`` method, a map with several most recent data objects can be retrieved, including their ordering. The ``contains(V value)`` method checks whether an object is currently found in the history - note that ValueHistories have size limits, therefore oldest values disappear from the history when new ones are added.

Adding a ``ValueHistory`` is very much alike to adding a ``Value``, just make sure to:

1. extend ``ValueHistory<>`` instead of ``Value<>``. If the history should keep more than the default 50 values, override the getMaxLimit() method to return your desired limit value.

2. in ``Context.java``, create a ``HistoryInterface`` instead of ``ValueInterface``.

How to add Updaters?
--------------------

New values can only flow into the Context over an ``Updater`` instance. Internal Updaters can be used by the dialog manager to actively add new values. External Updaters run in separate threads and query or listen for new values, for example over a ROS connection.

Updaters only add a single new data unit, relying on the ``AbstractValue.updateValue()`` method. Thanks to the inheritance chain, you can use an arbitrary Value or ValueHistory implementation as the target of an updater.

Adding an External Updater
""""""""""""""""""""""""""
Currently, there are two implementations of an External Updater: ``PeriodicUpdater`` and ``ROSTopicUpdater``.

``PeriodicUpdater`` calls an updating method after a certain time interval has passed. To use the periodic updating functionality:

1. Create a class extending ``PeriodicUpdater`` and implement its ``update()`` method. It should retrieve the values and finally add them over the ``target.updateValue(value)`` method call.

2. A constructor is required for the class. Simply match the PeriodicUpdater constructor and call ``super(target)`` within - or use the two-parameter constructor to change the update frequency (by default 1 second).

``ROSTopicUpdater`` subscribes itself to a ROS Topic and reacts to messages coming from the topic. To use:

1. Create a class extending ``ROSTopicUpdater`` and define the ``getTargetSubscriber()`` method, which will point the updater towards its target ROS topic. The options for the subscriber can be found in the ``RosSubscribers.java`` class.

2. Implement the ``update()`` method of the new class. This method will be called whenever a new message is stored in the internal ``message`` variable, so it might be enough to just call ``target.updateValue(message)``. If the data needs to be extracted from the message first, do it in the ``update()`` before calling ``target.updateValue``.

All External Updaters need to be initialized in the ``Context.java`` class. To do this:

1. Define the External Updater a private class variable to the ``Context.java`` class (look for the external updater definition section).

4. If the Updater depends on ROS, add its initialization into the ``Context.initializeROS(RosMainNode ros)`` method, otherwise add it to the private constructor ``Context()``. As the parameter, use the inner ``value`` or ``valueHistory`` variable from a ``ValueInterface`` or a ``HistoryInterface``.

Adding a new Internal Updater
"""""""""""""""""""""""""""""
1. Create a class extending InternalUpdater<*targetClass*, *valueType*>. The class and data type of the target ``Value`` or ``ValueHistory`` are the generic parameters for the updater.

2. A constructor is required for the class. Simply match the InternalUpdater constructor and call ``super(target)`` within. An example is in the ``DialogTopicsUpdater`` class.

4. Define the Internal Updater in the ``Context.java`` class. Initialize the updater within the private ``Context()`` constructor. For example:

``public final SampleUpdater SAMPLE_UPDATER; // Define as class variable``

``SAMPLE_UPDATER = new SampleUpdater(DIALOG_TOPICS.valueHistory); // Initialize in the constructor``


Adding generic Input- or OutputDevice
=====================================
In order to add new ``roboy.io.InputDevice`` and ``roboy.io.OutputDevice`` classes, changes in multiple locations are necessary.
1. Implement your ``InputDevice`` or ``OutputDevice`` implementation using ``class [YOUR CLASSNAME] extends InputDevice`` (or OutputDevice, if you're doing output).
2. If your device needs additional cleaning in order to be destroyed properly, additionally use ``implements CleanUp`` and implement the ``cleanup()`` method.
3. Add your devices to ``roboy.util.io`` in ``getInputs()`` and ``getOutputs()``, so the dialog system may use them if they're chosen in the configuration.
4. Add a (commented) input/output configuration to ``config.properties``.


.. highlight:: java

.. _tut_io_social:

Social Media Integration
========================


A new InputDevice for a social media
------------------------------------

First create a new class in roboy.io folder, namely "MySocialMediaInput" that implements from "roboy.io.InputDevice". 


    // inside MySocialMediaInput.java

    public class MySocialMediaInput implements InputDevice {

    }

One function called “listen()” has to be implemented.

    @Override
    public Input listen() throws InterruptedException, IOException {
        return null;
    }

Since you will have an "InputDevice" for each user then you need at least a unique identifier for each user right? So each of this unique identifiers should mapped to an "InputDevice". Therefore, create a static hashmap for it as follows.

.. NOTE::
    In further steps unique identifier mentioned as uuid


    private static final HashMap<String, MySocialMediaInput> inputDevices = new HashMap<>();


Add a constructor that receives the uuid as parameter

    // inside MySocialMediaInput.java

    public MySocialMediaInput(String uuid){
        //constructor
        synchronized(inputDevices){
            inputDevices.put(uuid, this)””
        }
    }

At this point, we received the uuid and have a hashmap of each "MySocialMediaInput". What else we need to implement? 
- Return messages as "roboy.io.Input" in the "listen()" method
- Receive the messages

.. Note::
    The order is actually reversed for the sake of tutorial of course you need to receive messages before you return them.

Let’s continue with first one. To return a message we need a message so create a "String" for it right below the "HashMap".

    private volatile String message;

We need to initialize it in constructor. Add the following into the beginning of constructor.

    // inside public MySocialMediaInput(String uuid)

    this.message = "";

Finally finish the listen method
    
    // inside MySocialMediaInput.java

    public Input listen() throws InterruptedException, IOException {
        Input newInput;
        syncronized(this){
            while(message.equals(“”)){
                try{
                    this.wait();
                }
                catch(InterruptedException e){
                    if(message == null||message.equals(“”)){
                        throw e;
                    }
                }
            }
            newInput = new Input(message);
            message = “”;
        }
        return newInput;
    }

Nice, now only thing to worry about is how to receive the message. 

Create a static "onUpdate(Pair<String, String>)" function that will be called from your "SocialMediaHandler" class with pair parameter that consits of the uuid and the message.

.. Note::
    There is no "SocialMediaHandler" as template. You should have a handler or any logic that receive the messages from your soical media. Then you need to call this function after applied your logic (e.g. wait for a certain time to answer.)

    
    public static void onUpdate(Pair<String, String> update){
        //get the uuid

        //get the inputdevice

        //assign the message to the input device
    }


.. Note::
    Pair "roboy.pair" that has to strings
    TO DO: 

To create the uuid that we discussed before, get the unique identifier from the "update". And add a social media name as prefix.

    //get the uuid

    String id = update.getKey();
    String uuid = "MySocialMedia-" + id;

.. Note::
    Why we add a prefix? Because it is possible if there is a same identifier from another social media. 

Now we need to get the input device there is an existing one with the uuid.
::
    //get the inputdevice

    MySocialMediaInput input = inputDevices.get(uuid);
    if (input == null){
	    try{
		    ConversationManager.spawnConversation(uuid);	
        }catch(IOException e){
            // do your logging or other error handling stuff
            return;
        }
        input = inputDevices.get(uuid);
    }

As you can see if there is no inputdevice with respective uuid. "ConversationManager.spawnConversation(uuid)" is used. It magically creates the inputDevice (as well as the Conversation and the magical stuff that you do not need to worry about)

Finally add another interface namely "CleanUp" and add its "cleanup()" method.
::
    // inside MySocialMediaInput.java

    public class MySocialMediaInput implements InputDevice, CleanUp {
        
        ...

        @override
        public void cleanup() {
            inputDevices.values().remove(this);
        }

    }

Done! Congratulations, you have just created your social media input device. . But it doesn’t work with only input device you also need to an output device for each conversation to send the output.

A new OutputDevice for a social media
-------------------------------------

You have perfectly working input device for your social media. But that only for receiving messages, we also need to send messages.

Create a new class in "roboy.io" folder namely "MySocialMediaOutput" that implements from "roboy.io.OutputDevice". 
::
    // inside MySocialOutput.java

    public class MySocialMediaOutput implements OutputDevice {

    }

You should override a method namely “act” and List of actions as parameter.
::
    @override
    public void act(List<Action> actions){
        // handle actions
    }

Leave the inside of the method empty for now.

As discussed before there is an OutputDevice for a user that is communicating with. And a unique identifier that is representing the user for each OutputDevice. Again just like our InputDevice you need a constructor and uuid as parameter.
::
    // inside MySocialOutput.java

    private String uuid;
    
    public MySocialMediaOutput(String uuid){
        //constructor
        this.uuid = uuid.substring(uuid.indexOf('-')+1);
    }

Remember the uuid in "MySocialMediaInput" was “MySocialMedia-”+id. Here it is splitted from the original user id that will be using for sending message.

Finish the "act" method
::
    // handle actions
    for(Action a : actions) {
        if (a instanceof SpeechAction) {
            // Normal text message
            String message = ((SpeechAction) a).getText();
            /* SEND THE MESSAGE with your social media handler or directly here the way is up to you */
        }else if (a instanceof EmotionAction) {
            String stickerID = null;
            switch(((EmotionAction) a).getState()){
                case "shy": /*use the method that sends an sticker or emoji or anything that shows emotions, again you can user your social media handler or any other method */
        break;
        }
    }

.. Note::
    In this tutorial, only shy emotion has been used, but there are several emotions you can check "roboy.emotions.RoboyEmotion.java" if you want more!

	/* */ these comments are not completed you should use your way that is sending a message via social media using the user’s id.


Telegram: Handle commands
-------------------------

New inline commands can be handled in "onUpdateReceived" method which is in "TelegramCommunicationHandler" class. 

Find the below if code block in onUpdateReceived.
::
    if(text.startsWith("/")){
        //command
    }

This block is only checking if the incoming message has a '/' at the beginning of the word, just like all the commands “/start”, “/stop”, “/desired_command”

Let's try to send a sticker after a command catch. Check if the command is "/like".
::
    //command
    if(text == “/like”){
        String stickerId = “CAADAgADOQAD5dCAEOtbfZz0NKh2Ag”
        sendSticker(chatID, stickerID)
    }

.. Note::
    Each sticker has its own unique id in Telegram.

