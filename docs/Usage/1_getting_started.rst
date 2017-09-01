Getting started
===============

How does it work?
-----------------

The basic NLP architecture is designed as a pipeline.
1. An input device (derived from ``de.roboy.io.InputDevice``) is producing text.
2. The text is is passed to a variety of linguistic analyzers (derived from ``de.roboy.linguistics.sentenceanalysis.Analyzer``). This currently consists of a Tokenizer and a POS tagger (both in ``de.roboy.linguistics.sentenceanalysis.SentenceAnalyzer``) but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools.
3. The results of all these linguistics analyzers are collected together with the original text and stored in an Interpretation instance. (``de.roboy.linguistics.sentenceanalysis.Interpretation``)
4. The interpretation ispassed on to a state machine (states are derived from ``de.roboy.dialog.personality.states.State``) within a personality class (derived from ``de.roboy.dialog.personality.Personality``) that decides how to react to the utterance. In the future, the intentions (``de.roboy.logic.Intention``) determined by the state machine will then formulated into proper sentences or other actions (``de.roboy.dialog.action.Action``) by a module called Verbalizer. Currently, these actions are still directly created in the personality class.
5. Finally, the created actions are sent to the corresponding output device (``de.roboy.io.OutputDevice``).

Design choices
--------------

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

How to extend it?
-----------------

Pick the corresponding interface, depending on which part of the system you want to extend. If you want to add new devices go for the input or output device interfaces. If you want to extend the linguistic analysis implement the Analyzer interface or extend the SentenceAnalyzer class. If you are happy with input, linguistics and output and just want to create more dialog, implement the Personality interface.

+--------------------+--------------------------------------------------+
| Create a new ...   | By implementing ...                              |
+====================+==================================================+
| Input Device       | de.roboy.io.InputDevice                          |
+--------------------+--------------------------------------------------+
| NLP Analyzer       | de.roboy.linguistics.sentenceanalysis.Analyzer   |
+--------------------+--------------------------------------------------+
| State Machine      | de.roboy.dialog.personality.Personality          |
+--------------------+--------------------------------------------------+
| State              | de.roboy.dialog.personality.states.State         |
+--------------------+--------------------------------------------------+
| Action type        | de.roboy.dialog.action.Action                    |
+--------------------+--------------------------------------------------+
| Output device      | de.roboy.io.OutputDevice                         |
+--------------------+--------------------------------------------------+

The interfaces are deliberately simple, containing only 0 - 2 methods that have to be implemented. Once you implemented your new classes include them in the personality used in ``de.roboy.dialog.DialogSystem``, if you only implemented single states or directly in ``de.roboy.dialog.DialogSystem`` for everything else.
