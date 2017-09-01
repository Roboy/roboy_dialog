Overview
========

To enable a natural way of communication, Roboy's Dialog Module implements a flexible architecture using personality classes, which each manage a number of different states. This enables us to spontaneously react to clues from the conversation partner and dynamically switch between purposes and stages of a dialog, mimicing a natural conversation.

Personality
===========

During one run-through, the Dialog System uses a single Personality instance (``de.roboy.dialog.personality.Personality``). A personality is designed to define how roboy reacts to every given situation, and as such Roboy can always only represent one personality at a time. Different personalities are meant to be used in different situations, like a more formal or loose one depending on the occasion.

The current primary personality is the SmallTalkPersonality (``de.roboy.dialog.personality.SmallTalkPersonality``).

State
=====

A state's activity can be divided into two stages. When the state is entered, the initial action from the act() method is carried out, which is expected to trigger a response from the person. After Roboy has received and analyzed the response, the react() method completes the current state's actions and Roboy moves on to the next state.



State - Action - Reaction
PersonalQAState - Ask a question about the person - Analzse and save the response
