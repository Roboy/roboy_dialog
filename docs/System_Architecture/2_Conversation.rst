************
Conversation
************

Is conglomerate concept, at its core, same name class

What is, what structure, what interact


A *Conversation* is a concept for everything necessary for conducting a dialog with a single counterpart. It's task is traversing through the different states of the interaction with the user to provide a maximally human-like experience.
It recieves Input through it's *InputDevice* and uses this Input in combination with the *InferenceEngine* and the *StateMachine* to traverse through the correct *States* as defined in the configured *Personality*. If it needs or learns information about the counterpart of the dialog, it uses the *Interlocutor* to access the Dialog Systems *Memory*. If it needs to store or inquire information about the environment it is in it uses its *Context* for this.

.. image:: images/DialogSystem_Conversation.png
  :alt: Dialog System Conversation Thread architecture

============
StateMachine
============

The ``roboy.dialog.DialogStateMachine`` is the unpopulated base for ``roboy.dialog.StateBasedPersonality``. It is unique to each *Conversation*, as each dialog usually is in a different state. It holds the *InferenceEngine* and the *Context* for a *Conversation*, as it should be the only part of it, accessing these. Its task is to handle traversing through the *States* of a *Personality* whereas a ``StateBasedPersonality`` enhances the ``DialogStateMachine``s features by augmenting the state-traversing functionalities with interaction-specific non-state behaviour like detecting the end of a conversation.

======================
Personality and states
======================

    Overview
    --------

    To enable a natural way of communication, Roboy's Dialog System implements a flexible architecture using different personalities defined in **personality files**. Each file represents a **state machine** and defines transitions between different **states**. This enables us to dynamically react to clues from the conversation partner and spontaneously switch between purposes and stages of a dialog, mimicing a natural conversation.


    Personality
    -----------

    A personality defines how Roboy reacts to every given situation. Different personalities are meant to be used in different situations, like a more formal or loose one depending on the occasion. Roboy always represents one personality at a time. Personalities are stored in JSON personality files.

    During one run-through, the Dialog System uses a single Personality instance (currently implemented in ``roboy.dialog.personality.StateBasedPersonality``) which is built on top of a state machine. This implementation loads the behaviour from a personality file that stores a representation of a state machine. Additionally, it is possible to define the dialog structure directly from code (as it was done in previous implementations).

    As the conversation goes on, the state machine will move from one state to another consuming inputs and producing outputs. The outputs are always defined by the current active state.


    State
    -----

    A state contains logic to control a small part of the conversation. It is a class that extends ``roboy.dialog.states.definitions.State`` and implements three functions: ``act()``, ``react()`` and ``getNextState()``.

    State's activity can be divided into three stages. First, when the state is entered, the initial action from the ``act()`` method is carried out, which is expected to trigger a response from the person. After Roboy has received and analyzed the response (see semantic parser), the ``react()`` method completes the current state's actions. Finally, Roboy picks a transition to the next state defined by the ``getNextState()`` method of the current state.

    State Output
    ------------
    The ``act()`` and ``react()`` functions return a ``State.Output`` object. This object defines what actions Roboy should do at this point of time. Most important actions include:

    - say a phrase
    - say nothing
    - end the conversation and optionally say a few last words

    The ``Output`` objects are created using static factory functions inside ``act()`` or ``react()`` in a very simple way. For example, if Roboy should react with a phrase, the ``react()`` function could be implemented like this: ``return Output.say("some text here")``. Here, ``Output.say`` is the static factory function that creates an ``Output`` object.

    To improve the dialog flow, you can add segues to the ``Output`` objects using ``outputObj.setSegue()``. A segue is a smooth transition from one topic to the next. It is also planned to add control over Roboy's face to the ``Output`` objects but this feature is not implemented yet.


    State Transitions
    -----------------

    A state can have any number of transitions to other states. Every transition has a name (like "next" or "error"). When changing states, the following state can be selected based on internal conditions of the current state. For example, a state can expect a "yes/no" answer and have tree outgoing transitions: "gotYes", "gotNo" and "askAgain" (if the reply is not "yes/no").

    When designing a new state, the transition names are defined first. The transition name should describe a condition and not another state. For example, a good name would be "knownPerson" (take this transition when you meet a known person) or "greetingDetected" (take this transition when you hear a greeting). In this case, the name only defines a condition and allows the transition to point to any state. In contrary, a bad name would be "goToQuestionAnsweringState" because it implies that no other state than QuestionAnsweringState should be attached to this transition. This breaks modularity.

    Once the state is implemented, the connections between states are defined in the personality file. At run time the state machine loads the file and initializes the transitions to point to correct states. During the implementation, the destination state can be retrieved by the transition name using ``getTransition(transitionName)``.

    It is possible to remain in the same state for many cycles. In this case the ``getNextState()`` method just returns a reference to the current state (``this``) and the ``act()`` and ``react()`` methods are carried out again. If ``getNextState()`` returns no next state (``null``), the conversation ends immediately.


    Fallback States
    ---------------

    Fallbacks are classes that handle unpredicted or unexpected input. A fallback can be attached to any state that expects inputs that it cannot deal with. In the case this state doesn't know how to react to an utterance, it can return ``Output.useFallback()`` from the ``react()`` function. The state machine will query the fallback in this case. This concept helps to keep the states simple and reduce the dependencies between them. When implementing the ``react()`` function of a new state, it is sufficient to detect unknown input and return ``Output.useFallback()``.

    In the current Dialog System, we use special states to implement the fallback functionality. A fallback state never becomes active so only the ``react()`` function has to be implemented. This function will be called if the active state returned ``Output.useFallback()``.


    State Parameters
    ----------------
    Sometimes you want to pass parameters to the states, for example define a path to a file that contains some data. Parameters are defined inside the personality file. Each parameter has a name and a string value. When a state is created, the state machine passes all parameters from the file to the state constructor. Therefore, every state sub-class should have a constructor that accepts parameters matching the constructor of the ``State`` class.

    During runtime, state objects can access the parameters using the ``getParameters()`` function with returns a ``StateParameters`` object. This object contains parameters from the personality file as well as references to ``StateMachine``, ``RosMainNode`` and ``Neo4jMemoryInterface`` in case you need them.


    State Interface
    ---------------

    When you create a new personality file you might forget to define important transitions and provide required parameters to some states. To prevent long debugging and find errors faster you can define an interface for every state. The interface describes:

    - transitions that have to be set
    - parameters that has to be provided
    - whether a fallback is required for this state

    After the personality file was loaded and the state machine was initialized, the dialog system will check if all states have everything they define in the state interface.

    For every state, its interface is implemented by overriding three functions: ``getRequiredTransitionNames()``, ``isFallbackRequired()`` and ``getRequiredParameterNames()``. Note, that you don't have to override those functions if your state has no specific requirements.



    Current standard Personality
    ----------------------------

    Current standard personality is used to interact with a single person. After Roboy hears a greeting and learns the name of the person, he will ask a few personal questions and answer some general questions about himself or the environment.

    .. figure:: images/ordinary_personality.png
    :alt: Current standard personality


    Overview over Implemented States
    --------------------------------

    **PassiveGreetingsState**: Roboy is listening until a greeting or his name is detected (passive state to start a conversation).

    **IntroductionState**: Roboy asks the interlocutor for his name, decides if the person is known and takes one of two transitions: knownPerson or newPerson.

    **PIAState** (PersonalInformationAskingState): Roboy asks one of the personal questions (like 'Where do you live?') and updates facts in Memory.

    **FUAState** (FollowUpAskingState): Roboy asks if the known facts are still up to date (like 'Do you still live in XY?').  This state is only entered if there are some known facts about the active interlocutor.

    **QuestionAnsweringState**: Roboy answers questions about itself or some general questions. Answers are provided by the parser (from sources like DBpedia) or the Memory.

    **WildTalkState**: This fallback state will query the deep learning generative model over ROS to create a reply for any situation.

    **FarewellState**: Roboy ends the conversation after a few statements.


=======
Context
=======

    The goal of ``roboy.context.Context`` is to collect information about Roboy's or a conversation's environment and state. It is a per-dialoge structure and will only store contemporary information which will be lost when the conversation. If the DialogSystem encounters information about this *Conversations* context, for example where this happens, a *Personality* might store this information here. Also external services may provide contextual information through the *Context*. This information can be used by the dialog manager and also to react upon situations that match certain conditions, such as turning the head of the Roboy when the Interlocutor moves or the last time a message from a user was recieved in a social-media setting.

    Architecture
    ------------

    .. figure:: ../images/context.png
     :alt: Context architecture

    The Context supports storing data as a ``Value`` or ``ValueHistory``. A ``Value`` only stores the latest data object that was pushed to it. A ``ValueHistory`` stores every value it receives and assigns each a unique key, thus the values can be ordered by their adding time.


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

================
Inference engine
================

    EXPERIMENTAL FUNCTIONALITY

    The Inference Engine is one of the main future components of Roboy Dialog System.
    Its main task is to process the data obtained from various analyzers and parsers to successfully infer
    the expected set of actions and retrieve the meaningful bits of information as well as ground the references
    from available ontologies and external sources.
