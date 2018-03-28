Semantic Parser
===============

Semantic parser is used to translate text representation into formal language representation. The aim is to be able to process user utterances and react upon them.

roboy_parser is based on `SEMPRE <http://nlp.stanford.edu/software/sempre/>`. It is currently being modified to fulfill Roboy Dialog system needs.


Installation
------------

In order to use semantic parser, you need to:

- clone ``roboy_parser`` repository::

	git clone http://github.com/Roboy/roboy_parser

- navigate to created repository::

	cd roboy_parser

- download dependencies::

    ./pull-dependencies roboy

- build it::

	mvn clean
	mvn install

- run it::

	mvn exec:java -Dexec.mainClass=edu.stanford.nlp.sempre.Main

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

roboy_parser is a separate Java project and is communicating using ``WebSocket``. Dialog system has a client implemented in ``SemanticParserAnalyzer.java`` class. It is therefore part of Natural Language Understanding unit.

Current parser was modified from SEMPRE and currently has following components

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

Usage
-----

In order to run the parser, you need to run **roboy_parser** first - see instructions on `project Github <http://github.com/Roboy/roboy_parser>` and then run Dialog System.

Configurations
""""""""""""""

To test parser, you can run following execution configurations using ``maven``. For more information refer to `project documentation <http://github.com/Roboy/roboy_parser>`

.. csv-table:: Possible parser configurations
  :header: "Command", "Options"
  :widths: 20, 40

    "interactive", "Standard lexicon and grammar, communication over terminal. Does not load all the models ahead. Error retrieval enabled."
    "demo-error", "Standard lexicon and grammar, communication over socket. Loads all the models ahead. Error retrieval enabled"
    "demo", "Standard lexicon and grammar, communication over socket. Loads all the models ahead. Error retrieval disabled"
    "debug", "Standard lexicon and grammar, communication over server (web page available). Does not load all the models ahead. Error retrieval enabled"
