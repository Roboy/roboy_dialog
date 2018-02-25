package roboy.newDialog.states;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;

import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.util.QAFileParser;
import roboy.util.UzupisIntents;

import java.io.IOException;
import java.util.*;

import static roboy.linguistics.Linguistics.OBJ_ANSWER;
import static roboy.util.UzupisIntents.*;


/**
 * A class that will issue a naturalization certificate for the Republic of Uzupiz
 * Asks a few personal questions and can automagically generate a pdf with a certificate and print it (python script)
 */
public class UzupisState extends State {

    private final Logger logger = LogManager.getLogger();
    private final String QAFILEPATH = "QAFilePath";

    private ArrayList<UzupisIntents> alreadyAsked;
    private final int toAskCounter = 7;
    private UzupisIntents currentIntent;

    private Map<String, List<String>> questions;
    private Map<String, List<String>> successAnswers;
    private Map<String, List<String>> failureAnswers;

    public Interlocutor person;

    public UzupisState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);

        QAFileParser parser = new QAFileParser(params.getParameter(QAFILEPATH));

        questions = parser.getQuestions();
        successAnswers = parser.getSuccessAnswers();
        failureAnswers = parser.getFailureAnswers();

        alreadyAsked = new ArrayList<>();

        // TODO proper Interlocutor handling via Context
        person = new Interlocutor();
    }

    @Override
    public Output act() {

        // TODO implement special treatment for name -> interlocutor

        String toAsk;

        do {
            currentIntent = UzupisIntents.randomIntent();
        } while (alreadyAsked.contains(currentIntent));

        alreadyAsked.add(currentIntent);
        toAsk = questions.get(currentIntent.toString()).get(
                (int) Math.random() * questions.get(currentIntent.toString()).size());
        return Output.say(toAsk);
    }

    @Override
    public Output react(Interpretation input) {
        String answer = (String) input.getFeature(OBJ_ANSWER);
        String toAnswer;
        if (answer.length()>2)
        {
            person.saveUzupisProperty(currentIntent, answer);
            toAnswer = successAnswers.get(currentIntent.toString()).get(
                    (int) Math.random() * successAnswers.get(currentIntent.toString()).size());
            toAnswer = String.format(toAnswer, answer);
        }
        else {
            person.saveUzupisProperty(currentIntent, "classified information");
            toAnswer = failureAnswers.get(currentIntent.toString()).get(
                    (int) Math.random() * failureAnswers.get(currentIntent.toString()).size());
        }

        return Output.say(toAnswer);

    }

    @Override
    public State getNextState() {
        if (alreadyAsked.size() != toAskCounter) {
            return getTransition("loop");
        }
        String color = person.getUzupisInfo().get(COLOR);
        String plant = person.getUzupisInfo().get(PLANT);
        String  animal = person.getUzupisInfo().get(ANIMAL);
        String word = person.getUzupisInfo().get(WORD);
        String name = person.getUzupisInfo().get(NAME);
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "/home/roboy/cognition_ws/src/roboy_cognition/roboy_dialog/resources/scripts/uzupizer.py", "0", color, plant, animal, word, name, "2018");
            pb.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return getTransition("next");

    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QAFILEPATH);
    }

}
