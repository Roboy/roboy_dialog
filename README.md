# DialogSystem

## What is it?

This repository contains a dialog system developed for the humanoid robot Roboy (roboy.org). 

## How does it work?

The basic NLP architecture is designed as a pipeline. An input device (de.roboy.io.InputDevice) is producing text, which is passed to a variety of linguistic analyzers (de.roboy.linguistics.sentenceanalysis.Analyzer). This currently consists of a Tokenizer and a POS tagger but could in the future be accompanied by named entity recognition, a syntactical and semantical analysis, an interpretation of the sentence type or other tools. The results of all these linguistics analyzers are collected together with the original text (de.roboy.linguistics.sentenceanalysis.Interpretation) and passed on to a state machine (de.roboy.dialog.personality.Personality) that decides how to react to the utterance. The intentions (de.roboy.logic.Intention) determined by the state machine are then formulated into proper sentences or in the future other actions (de.roboy.dialog.action.Action) by a module called Verbalizer. The actions created by the Verbalizer are sent to the corresponding output device (de.roboy.io.OutputDevice).

There are interfaces for each step in the processing pipeline to enable an easy exchange of elements. The goal would be to easily exchange personalities based on the occasion.

The implementation of the pipeline is in Java. Integrations with tools in other languages, like C++ RealSense stuff, should be wrapped in a module in the pipeline.

## How to run it?

The repository contains a repository that can be readily imported into Eclipse. The code can be executed by running de.roboy.dialog.DialogSystem.

## How to extend it?

|Create a new ...|By implementing ...    |
|----------------|-----------------------|
|Input Device    |de.roboy.io.InputDevice|
|NLP Analyzer    |de.roboy.linguistics.sentenceanalysis.Analyzer|
|State Machine   |de.roboy.dialog.personality.Personality|
|State           |de.roboy.dialog.personality.states.State|
|Intention       |de.roboy.logic.Intention|
|Action type     |de.roboy.dialog.action.Action|
|Output device   |de.roboy.io.OutputDevice|

Then include your new class in the personality used in de.roboy.dialog.DialogSystem (for states) or directly in de.roboy.dialog.DialogSystem (everything else) correspondingly.
