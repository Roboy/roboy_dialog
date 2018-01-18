Personality and states
======================
Overview
--------

To enable a natural way of communication, Roboy's Dialog Module implements a flexible architecture using personality classes, which each manage a number of different states. This enables us to dynamically react to clues from the conversation partner and spontaneously switch between purposes and stages of a dialog, mimicing a natural conversation.

Personality
-----------

During one run-through, the Dialog System uses a single Personality instance (``de.roboy.dialog.personality.Personality``). A personality is designed to define how roboy reacts to every given situation, and as such Roboy can always only represent one personality at a time. Different personalities are meant to be used in different situations, like a more formal or loose one depending on the occasion.

The current primary personality is the SmallTalkPersonality (``de.roboy.dialog.personality.SmallTalkPersonality``).

State
-----

A state's activity can be divided into two stages. When the state is entered, the initial action from the ``act()`` method is carried out, which is expected to trigger a response from the person. After Roboy has received and analyzed the response, the ``react()`` method completes the current state's actions and Roboy moves on to the next state.

The AbstractBooleanState describes a special case where the state's reaction depends on whether the ``act()`` method resulted in successful interaction. States which implement AbstractBooleanState can respond differently move on into different stages according to their ``determineSuccess()`` method.

For example, the intial action of ``de.roboy.dialog.personality.states.IntroductionState`` is to ask the user's name. Then the response is analyzed externally and when the state's determineSuccess() method is called, it checks whether a name was extracted. If this is the case, then the system outputs predefined sentences with the extracted name embedded into them. Otherwise, fallback sentences are used which do not include a name.

State machine structure
-----------------------

Every state defines at least one successor state, and more complex hierarchies can be realized - for example as a fallback system for cases when a single state cannot respond in a meaningful manner. The following is an example from the documentation of SmallTalkPersonality:

The current state machine looks like this:

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

 The Question Randomizer, Question Answering, Segue and Wilk talk states are stacked. If one cannot give an appropriate reaction to the given utterance, the utterance is passed on to the next one. The Wild talk state will always answer.


