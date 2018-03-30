Semantic Parser
===============

Semantic parser is used to translate text representation into formal language representation. The aim is to be able to process user utterances and react upon them.

roboy_parser is based on `SEMPRE <http://nlp.stanford.edu/software/sempre/`. It is currently being modified to fulfill Roboy Dialog system needs.


Installation
------------

In order to use semantic parser, you need to:

- clone ``roboy_parser`` repository::

	git clone http://github.com/Roboy/roboy_parser

- navigate to created repository::

	cd roboy_parser

- build it::

	ant freebase

- run it::

	./quick_start [options]

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

Functionalities
"""""""""""""""

Roboy parser currently has currently following functionalities:

.. csv-table:: Semantic Parser algorithms used
  :header: "Functionality", "Software used", "Summary"
  :widths: 20, 20, 60

  "POS Tags", "OpenNLP", "Tagging tokens as part of speech"
	"NER Tags", "OpenNLP", "Tool used to tag named entities like PERSON, NUMBER, ORGANIZATION"
  "Triple extraction", "OpenIE", "Tool used to extract triples from sentences in form ``(Subject,Predicate,Object)``"

Usage
-----

In order to run the parser, you need to run **roboy_parser** first - see instructions on `project Github <http://github.com/Roboy/roboy_parser` and then run Dialog System.

Configurations
""""""""""""""

To test parser, you can run following configurations using ``quick_start.sh`` script. For more information refer to `project documentation <http://github.com/Roboy/roboy_parser`

.. csv-table:: Possible parser configurations
  :header: "Command", "Options"
  :widths: 20, 40

  "./quick_start", "Default configuration. Using custom Roboy grammar and lexicons"
	"./quick_start freebase", "Example setup to test Freebase functionality"
