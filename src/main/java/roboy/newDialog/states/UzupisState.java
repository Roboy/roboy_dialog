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
    private final int toAskCounter = UzupisIntents.values().length;
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

        person = Context.getInstance().ACTIVE_INTERLOCUTOR.getValue();
    }

    @Override
    public Output act() {

        String toAsk;

        if (alreadyAsked.isEmpty()) {
            currentIntent = UzupisIntents.INTRO;
        }
        else if (alreadyAsked.size()==1 && person.getName() == null) {
            currentIntent = UzupisIntents.NAME;
        }
        else {
            if (person.getName() != null && !alreadyAsked.contains(NAME)) {
                alreadyAsked.add(UzupisIntents.NAME);
            }
            do {
                currentIntent = UzupisIntents.randomIntent();
            } while (alreadyAsked.contains(currentIntent));
        }

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
            if (currentIntent==UzupisIntents.NAME) {
                person.addName(answer);
            }
            else if(currentIntent!=UzupisIntents.INTRO) {
                person.saveUzupisProperty(currentIntent, answer);
            }
            toAnswer = successAnswers.get(currentIntent.toString()).get(
                    (int) Math.random() * successAnswers.get(currentIntent.toString()).size());
            toAnswer = String.format(toAnswer, answer);
        }
        else {
            if (currentIntent!=UzupisIntents.INTRO) {
                person.saveUzupisProperty(currentIntent, "classified information");
            }
            if (currentIntent==UzupisIntents.NAME) {
                person.addName("human");
            }
            toAnswer = failureAnswers.get(currentIntent.toString()).get(
                    (int) Math.random() * failureAnswers.get(currentIntent.toString()).size());
        }

        return Output.say(toAnswer);

    }

    @Override
    public State getNextState() {

        Context.getInstance().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);

        if (alreadyAsked.size() != toAskCounter) {
            return getTransition("loop");
        }
        String color = person.getUzupisInfo().get(COLOR);
        String plant = person.getUzupisInfo().get(PLANT);
        String animal = person.getUzupisInfo().get(ANIMAL);
        String word = person.getUzupisInfo().get(WORD);
        String name = person.getName();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "/home/roboy/cognition_ws/src/roboy_cognition/roboy_dialog/resources/scripts/uzupizer.py", "0", color, plant, animal, word, name, "2018");
            pb.start();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return getTransition("next");

    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QAFILEPATH);
    }

}
