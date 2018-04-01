Tutorials
=========

.. highlight:: java

Adding a New State
------------------

Roboy often visits different events and you might want him to say something specific, for example mention a company or a sponsor. One way to do this would be to modify an existing state. However, these changes are often discarded as you still want to have the old behaviour. There is a better way: create a new custom state specifically for your needs.

In this tutorial you will learn how to design and implement a new state. To keep everything simple, the state will just ask a yes-no question and listen to the answer. Based on the answer, you will pick one of two replies and choose one of two transitions.

Let's start! We are going to add a state that tests whether the interlocutor (person speaking to Roboy) knows some basic math. First, create a new class named ``DoYouKnowMathState`` that extends from ``roboy.dialog.states.definitions.State``::

    // inside DoYouKnowMathState.java

    public class DoYouKnowMathState extends State {

    }

Your IDE will notify you that three functions (``act()``, ``react(...)`` and ``getNextState()``) have to be implemented. Let's add them::

    // inside DoYouKnowMathState.java

    @Override
    public Output act() {
        return null;
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public State getNextState() {
        return null;
    }

Additionally, we need a special constructor and a new variable to store the next state for later::

    // inside DoYouKnowMathState.java

    private State next;

    public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

Now, we can write some logic and define what our new state should do. The ``act()`` function is always executed first. In our case, we want to ask a simple question. Replace ``return null;`` inside ``act()`` with following::

    // inside public Output act()

    return Output.say("What is 2 plus 2?");

The interlocutor's answer will be passed to the ``react(...)`` function once it is available. Inside, we should check if the answer is correct and react based on correctness. This code is one of the simplest ways to do this::

    // inside public Output react(Interpretation input)

    // get tokens (= single words of the input)
    String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

    // check if the answer is correct (simplest version)
    if (tokens.length > 0 && tokens[0].equals("four")) {
        // answer correct
        next = getTransition("personKnowsMath");
        return Output.say("You are good at math!");
    } else {
        // answer incorrect
        next = getTransition("personDoesNotKnowMath");
        return Output.say("Well, 2 plus 2 is 4!");
    }

Note a few things here:

- to keep this tutorial simple, we only check if the first word of the reply equals "four"
- based on reply correctness, we get the next state using ``getTransition(<transitionName>)`` save it for later
- similarly to ``act()``, we define the output with ``return Output.say(<stringToSay>);``

Finally, we can implement the last required function ``getNextState()`` that defines the next state to enter. Inside, we just return the next state that we defined inside ``react(...)``::

    // inside public State getNextState()

    return next;

That's it, you have just created your first state! Here is how the class should look like::

    // inside DoYouKnowMathState.java

    package roboy.dialog.tutorials.tutorialStates;

    import roboy.dialog.states.definitions.State;
    import roboy.dialog.states.definitions.StateParameters;
    import roboy.linguistics.Linguistics;
    import roboy.linguistics.sentenceanalysis.Interpretation;

    public class DoYouKnowMathState extends State {

        private State next;

        public DoYouKnowMathState(String stateIdentifier, StateParameters params) {
            super(stateIdentifier, params);
        }

        @Override
        public Output act() {
            return Output.say("What is 2 plus 2?");
        }

        @Override
        public Output react(Interpretation input) {

            // get tokens (= single words of the input)
            String[] tokens = (String[]) input.getFeatures().get(Linguistics.TOKENS);

            // check if the answer is correct (simplest version)
            if (tokens.length > 0 && tokens[0].equals("four")) {
                // answer correct
                next = getTransition("personKnowsMath");
                return Output.say("You are good at math!");

            } else {
                // answer incorrect
                next = getTransition("personDoesNotKnowMath");
                return Output.say("Well, 2 plus 2 is 4!");
            }
        }

        @Override
        public State getNextState() {
            return next;
        }
    }


The newest version of the complete code can be found in in ``roboy.dialog.tutorials.tutorialStates.DoYouKnowMathState``. Read the :ref:`tut_new_personality` tutorial to learn how to connect your new state with others.

When using the new state, you could encounter the conversation::

    [Roboy]: What is 2 plus 2?
    [You]:   four
    [Roboy]: You are good at math!

Or, if you provide a wrong answer::

    [Roboy]: What is 2 plus 2?
    [You]:   one
    [Roboy]: Well, 2 plus 2 is 4!

To learn more details about states and personalities, refer to the :ref:`personality_and_states` page. There, you will find details about state fallbacks, parameters and interfaces, as well as more information about different personalities and more output options.

.. _tut_new_personality:

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