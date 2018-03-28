Personality and states
======================
Overview
--------

To enable a natural way of communication, Roboy's Dialog System implements a flexible architecture using different personalities defined in personality files. Each file represents a state machine and defines transitions between different states. This enables us to dynamically react to clues from the conversation partner and spontaneously switch between purposes and stages of a dialog, mimicing a natural conversation.


Personality
-----------

A personality defines how roboy reacts to every given situation. Different personalities are meant to be used in different situations, like a more formal or loose one depending on the occasion. Roboy always represents one personality at a time. Personalities are stored in JSON personality files.

During one run-through, the Dialog System uses a single Personality instance (currently implemented in ``roboy.dialog.personality.StateBasedPersonality``) which is built on top of a state machine. This implementation loads the behaviour from a personality file that stores a representation of a state machine. Additionally, it is possible to define the dialog structure directly from code (as it was done in previous implementations).

As the conversation goes on, the state machine will move from one state to another consuming inputs and producing outputs. The outputs are always defined by the current active state.


State
-----

A state contains logic to control a small part of the conversation. It is a class that extends ``roboy.dialog.states.State`` and implements three functions: ``act()``, ``react()`` and ``getNextState()``.

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

A state can have any number of transitions to other states. Every transition has a name (like "next" or "errorState"). When changing states, the following state can be selected based on internal conditions of the current state. For example, a state can expect a "yes/no" answer and have tree outgoing transitions: "gotYes", "gotNo" and "askAgain" (if the reply is not "yes/no"). 
 
When designing a new state, only the transition names are defined. The connections between states are defined in the personality file later. At run time the state machine loads the file and initializes the transitions to point to correct states. The destination state can be retrieved by the transition name using ``getTransition(transitionName)``.

It is possible to remain in the same state for many cycles. In this case the ``getNextState()`` method just returns a reference to the current state (``this``) and the ``act()`` and ``react()`` methods are carried out again. If ``getNextState()`` returns no next state (``null``), the conversation ends immediately.


Fallback States
---------------

Fallbacks are classes that handle unpredicted or unexpected input. A fallback can be attached to any state that expects inputs that it cannot deal with. In the case this state doesn't know how to react to an utterance, it can return ``Output.useFallback()`` from the ``react()`` function. The state machine will query the fallback in this case. This concept helps to keep the states simple and reduce the dependencies between them. When implementing the ``react()`` function of a new state, it is sufficient to detect unknown input and return ``Output.useFallback()``.

In the current Dialog System, we use special states to implement the fallback functionality. A fallback state never becomes active so only the ``react()`` function has to be implemented. This function will be called if the active state returned ``Output.useFallback()``.


State Parameters
----------------
TODO


State Interface
---------------

TODO: Define required transitions, required parameter names and fallback availability to detect errors in the personality file during loading.


Current 'standard' personality
------------------------------
TODO: picture + short description



Overview over Implemented States
--------------------------------

TODO: most important states


Tutorial: Creating a New State
------------------------------

TODO: create a new state that has specified behaviour


Tutorial: Creating a New Personality
------------------------------------

TODO: create a new personality file
