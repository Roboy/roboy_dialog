***************
Getting started
***************

How does it work?
=================

The basic NLP architecture is designed as a pipeline.

1. An input device (derived from ``de.roboy.io.InputDevice``) is producing text.

2. The text is is passed to a variety of linguistic analyzers (derived from ``de.roboy.linguistics.sentenceanalysis.Analyzer``). This currently consists of a Tokenizer and a POS tagger (both in ``de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer``) but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools.

3. The results of all these linguistics analyzers are collected together with the original text and stored in an Interpretation instance. (``de.roboy.linguistics.sentenceanalysis.Interpretation``)

4. The interpretation ispassed on to a state machine (states are derived from ``de.roboy.dialog.personality.states.State``) within a personality class (derived from ``de.roboy.dialog.personality.Personality``) that decides how to react to the utterance. In the future, the intentions (``de.roboy.logic.Intention``) determined by the state machine will then formulated into proper sentences or other actions (``de.roboy.dialog.action.Action``) by a module called Verbalizer. Currently, these actions are still directly created in the personality class.

5. Finally, the created actions are sent to the corresponding output device (``de.roboy.io.OutputDevice``).

Design choices
==============

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

How to extend it?
=================

If you do not know how the system works please refer the "System architecture" section. If you know the part you want to extend or are sure you do not need to understand the detailed functionality of the overall system you may directly start with the "Developer manual" section. The :ref:`Code structure overview` section is a good place to start.