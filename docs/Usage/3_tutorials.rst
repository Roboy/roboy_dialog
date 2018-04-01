Tutorials
=========


Adding a New State
------------------

Roboy often visits different events and you might want him to say something specific, for example mention a company or a sponsor. One way to do this would be to modify an existing state. However, these changes are often discarded as you still want to have the old behaviour. There is a better way: create a new custom state specifically for your needs.

In this tutorial you will learn how to design and implement a new state. To keep everything simple, the state will just ask a yes-no question and listen to the answer. Based on the answer, you will pick one of two replies and choose one of two transitions.

Let's start! We are going to add a state that tests whether the interlocutor (person speaking to Roboy) knows some basic math. First, create a new class named ``DoYouKnowMathState`` that extends from ``roboy.dialog.states.definitions.State``::

    .. code-block:: java
    public class DoYouKnowMathState extends State {

    }





Creating a New Personality
--------------------------


Adding New Questions to the State
---------------------------------


Querying the Memory from the Dialog System
------------------------------------------


Creating a Value History
------------------------


Storing and Updating Values in the Context
------------------------------------------


Extending the Lexicon and the Grammar
-------------------------------------

This tutorial explains how to create or change grammar and lexicon used in the semantic parser.

Lexicon
"""""""

To create your own custom lexicon, you need to create a new file or copy an existing lexicon and add lexemes in the following format::

    {lexeme:"LEXEME", formula:"FORMULA", type:"TYPE"}

where:

- lexeme - is a natural language utterance, e.g., name

- formula - is a semantic representation of the lexeme, e.g., rb:HAS_NAME

- type - is a lexeme type, e.g., NamedEntity, fb:type.any

Additionally, you can also add features in JSON format for map::

    {lexeme:"name", formula:"rb:HAS_NAME", type:"DataProperty", features:"{feature1:0.5, feature2:0.3}"}

Grammar
"""""""

To create your own custom grammar, you need to create a new file or copy existing grammar and add rules in the following format::

    (rule [Category] ([Expression]) ([Function]))

where:

- Category - is a category of rule, for root derivation use $ROOT

- Expression - is a format of text accepted by the rule expressed in your custom categories or names, e.g., $PHRASE, $TOKEN, $Expr

- Function - semantic function that should be applied to specified pattern, e.g., IdentityFn

Example rules::

    (rule $ROOT ((what optional) (is optional) $Expr (? optional)) (IdentityFn))
    (rule $Expr ($Expr $Conversion) (JoinFn backward))

For in-depth tutorial on expression and function types, refer to original SEMPRE `tutorial <https://github.com/percyliang/sempre/blob/master/TUTORIAL.md>`_ or `documentation <https://github.com/percyliang/sempre/blob/master/DOCUMENTATION.md>`_

Used files in configuration
"""""""""""""""""""""""""""

To use created files, you need to set the correct parameter in ``pom.xml`` file.
For grammar::

    -Grammar.inPaths

For lexicon::

    -SimpleLexicon.inPaths

Scoring Functions and Knowledge Retrieval
-----------------------------------------

Currently, our semantic parser uses error retrieval mechanism that can be modified in the following steps:

Scoring Function
""""""""""""""""

1. Move to package::

    edu.stanford.nlp.sempre.roboy.score

2. Implement ``edu.stanford.nlp.sempre.roboy.score.ScoringFunction`` class with score method.

3. Add scoring function in constructor of ``edu.stanford.nlp.sempre.roboy.ErrorRetrieval`` class.

Knowledge Retriever
"""""""""""""""""""

1. Move to package::

    edu.stanford.nlp.sempre.roboy.error

2. Implement ``edu.stanford.nlp.sempre.roboy.error.KnowledgeRetriever`` class with analyze method.

3. Add knowledge retriever in constructor of ``edu.stanford.nlp.sempre.roboy.ErrorRetrieval`` class.

Editing the Config File
-----------------------