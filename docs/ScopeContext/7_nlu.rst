Natural Language Understanding (NLU)
====================================

The NLU submodule is used to translate text inputs into formal semantic representations. This allows for capturing the semantic intent behind a statement or question, and using knowledge bases to translate formal question representations into answers.

The roboy_parser NLU module is based on `SEMPRE <http://nlp.stanford.edu/software/sempre/>`. It is currently being modified to fulfill Roboy Dialog system needs.


Installation
------------

The NLU module is installed automatically when running `mvn clean install` in roboy_dialog.

Architecture
------------

Semantic parser is based on the language model and NLP algorithms that then apply rules to the utterance to translate it. Language model consists of:
- set of grammar rules,
- lexicon,
- training dataset.

General architecture can be seen on the diagram below.

.. figure:: ../images/parser.png
:alt: Semantic parser general architecture

Implementation
--------------

NLU is a JAR dependency, with which the dialog system is communicating through the `edu.stanford.nlp.sempre.roboy.SemanticAnalyzerInterface` class. Dialog system has a client implemented in ``SemanticParserAnalyzer.java`` class.

The current parser was modified from SEMPRE and currently has following components

.. figure:: ../images/PARSER_arch.png
:alt: Semantic parser components

Functionalities
"""""""""""""""

Roboy parser currently has currently following functionalities:

.. csv-table:: Semantic Parser algorithms used
  :header: "Functionality", "Software used", "Summary"
  :widths: 20, 20, 60

  "Tokens", "OpenNLP", "Tokenized utterance"
  "POS Tags", "OpenNLP", "Tagging tokens as part of speech"
  "NER Tags", "OpenNLP", "Tool used to tag named entities like PERSON, NUMBER, ORGANIZATION"
  "Triple extraction", "OpenIE", "Tool used to extract triples from sentences in form ``(Subject,Predicate,Object)``"
  "Parser result", "Parser", "Logical representation of an utterance"
  "Parser answer", "Parser", "Answer for resulting parser result"
  "Follow-up", "Parser", "Follow-up questions for underspecified term"
