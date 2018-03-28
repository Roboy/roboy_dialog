Personality and states
======================
Overview
--------

To enable a natural way of communication, Roboy's Dialog Module implements a flexible architecture using personality classes, which each manage a number of different states. This enables us to dynamically react to clues from the conversation partner and spontaneously switch between purposes and stages of a dialog, mimicing a natural conversation.

Personality
-----------

During one run-through, the Dialog System uses a single Personality instance (``de.roboy.dialog.personality.Personality``). A personality is designed to define how roboy reacts to every given situation, and as such Roboy can always only represent one personality at a time. Different personalities are meant to be used in different situations, like a more formal or loose one depending on the occasion.

The current primary personality is the SmallTalkPersonality (``de.roboy.dialog.personality.SmallTalkPersonality``).

A new personality (``roboy.dialog.personality.StateBasedPersonality``) is currently being implemented. Similarly to the SmallTalkPersonality, it is built on top of a state machine. The new personality is designed to be more generic one and allows to load the behaviour from a personality file. The file stores a representation of the state machine. Additionally, it is still possible to define the dialog structure directly from code (as it was done in previous implementations).

Legacy State
------------

A state's activity can be divided into two stages. When the state is entered, the initial action from the ``act()`` method is carried out, which is expected to trigger a response from the person. After Roboy has received and analyzed the response, the ``react()`` method completes the current state's actions and Roboy moves on to the next state.

The AbstractBooleanState describes a special case where the state's reaction depends on whether the ``act()`` method resulted in successful interaction. States which implement AbstractBooleanState can respond differently move on into different stages according to their ``determineSuccess()`` method.

For example, the initial action of ``de.roboy.dialog.personality.states.IntroductionState`` is to ask the user's name. Then the response is analyzed externally and when the state's determineSuccess() method is called, it checks whether a name was extracted. If this is the case, then the system outputs predefined sentences with the extracted name embedded into them. Otherwise, fallback sentences are used which do not include a name.

New State
---------

Currently, we are improving the state system. Old state implementations will be replaced with newer ones. The functionality of the AbstractBooleanState will be improved to allow arbitrary conditional transitions in every new state. Nested states will be replaced with the fallback concept.

A state's activity can be divided into two stages. When the state is entered, the initial action from the ``act()`` method is carried out, which is expected to trigger a response from the person. After Roboy has received and analyzed the response, the ``react()`` method completes the current state's actions and Roboy picks a transition to the next state defined by the ``getNextState()`` method of the current state.

It is possible to remain in the same state for many cycles. In this case the ``getNextState()`` method just returns a reference to the current state (``this``) and the ``act()`` and ``react()`` methods are carried out again.

A state can have any number of transitions to other states. Every transition has a name (like "next" or "errorState"). When changing states, the following state can be selected based on internal conditions of the current state. For example, a state can expect a "yes/no" answer and have tree outgoing transitions: "gotYes", "gotNo" and "askAgain" (if the reply is not "yes/no"). 
 
When designing a new state, only the transition names are defined. The following states are defined in the personality file later. At run time the state machine loads the file and initializes the transitions to point it correct states. The destination state can be retrieved by the transition name using ``getTransition(transitionName)``.

A fallback can be attached to a state. In the case this state doesn't know how to react to an utterance, it can return ``ReAct.useFallback()`` from the ``react()`` function. The state machine will query the fallback in this case. This concept helps to decouple the states and reduce the dependencies between them. When implementing the ``react()`` function of a new state, it is sufficient to detect unknown input and return ``ReAct.useFallback()``.




Legacy State machine structure
------------------------------

Every state defines at least one successor state, and more complex hierarchies can be realized - for example as a fallback system for cases when a single state cannot respond in a meaningful manner. The fallback system implemented using nested states in the legacy state machine and will be improved in the newer implementation. The following is an example from the documentation of SmallTalkPersonality:

The current legacy state machine looks like this:

Greeting state
      |
      V
 Introduction state
      |
      V
 Question Randomizer state
  |_Question Answering state
    |_Segue state
      |_Wild talk state

 The Question Randomizer, Question Answering, Segue and Wilk talk states are nested. If one cannot give an appropriate reaction to the given utterance, the utterance is passed on to the next one. The Wild talk state will always answer.


