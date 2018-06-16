package roboy.dialog.states.eventStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;

import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.nodes.Interlocutor;
import roboy.util.QAFileParser;
import roboy.util.UzupisIntents;

import java.io.IOException;
import java.util.*;

import static roboy.util.UzupisIntents.*;


/**
 * A class that will issue a naturalization certificate for the Republic of Uzupiz
 * Asks a few personalStates questions and can automagically generate a pdf with a certificate and print it (python script)
 */
public class UzupisState extends State {

    private final Logger logger = LogManager.getLogger();
    private final String QAFILEPATH = "QAFilePath";
    private final String CERTIFICATESGENERATOR = "CertificatesGeneratorPath";

    private ArrayList<UzupisIntents> alreadyAsked;
    private final int toAskCounter = UzupisIntents.values().length;
    private UzupisIntents currentIntent;

    private Map<String, List<String>> questions;
    private Map<String, List<String>> successAnswers;
    private Map<String, List<String>> failureAnswers;

    private String CertificatesGeneratorScript;

    public Interlocutor person;

    public UzupisState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);

        QAFileParser parser = new QAFileParser(params.getParameter(QAFILEPATH));
        CertificatesGeneratorScript = params.getParameter(CERTIFICATESGENERATOR);

        questions = parser.getQuestions();
        successAnswers = parser.getSuccessAnswers();
        failureAnswers = parser.getFailureAnswers();

        alreadyAsked = new ArrayList<>();

        person = getContext().ACTIVE_INTERLOCUTOR.getValue();
    }

    @Override
    public Output act() {

        String toAsk;

        if (alreadyAsked.isEmpty()) {
            currentIntent = UzupisIntents.INTRO;
        } else if (alreadyAsked.size()==1 && person.getName() == null) {
            currentIntent = UzupisIntents.NAME;
        } else {
            if (person.getName() != null && !alreadyAsked.contains(NAME)) {
                alreadyAsked.add(UzupisIntents.NAME);
            }
            do {
                currentIntent = UzupisIntents.randomIntent();
            } while (alreadyAsked.contains(currentIntent));
        }

        alreadyAsked.add(currentIntent);
        toAsk = questions.get(currentIntent.toString()).get(
                (int) (Math.random() * questions.get(currentIntent.toString()).size()));
        return Output.say(toAsk);
    }

    @Override
    public Output react(Interpretation input) {
        String answer = input.getAnswer();
        String toAnswer = "";
        if (answer != null) {
            if (answer.length() > 2) {
                if (currentIntent == UzupisIntents.NAME) {
                    person.addName(answer);
                } else if (currentIntent != UzupisIntents.INTRO) {
                    person.saveUzupisProperty(currentIntent, answer);
                }
                toAnswer = successAnswers.get(currentIntent.toString()).get(
                        (int) (Math.random() * successAnswers.get(currentIntent.toString()).size()));
                toAnswer = String.format(toAnswer, answer);
            } else {
                if (currentIntent != UzupisIntents.INTRO) {
                    person.saveUzupisProperty(currentIntent, "classified information");
                }
                if (currentIntent == UzupisIntents.NAME) {
                    person.addName("human");
                }
                toAnswer = failureAnswers.get(currentIntent.toString()).get(
                        (int) (Math.random() * failureAnswers.get(currentIntent.toString()).size()));
            }
        }

        // TODO add on final uzupis utterance
        // "I hereby declare on oath, that I absolutely and entirely " +
//        "renounce and abjure allegiance and fidelity to any prince, potentate, " +
//                "state, or sovereignty, of whom or which I have therefore been a subject " +
//                "or citizen; that I will support and defend the Constitution of the Republic " +
//                "of Uzupis against all enemies, foreign and domestic; that I will bear true faith " +
//                "and allegiance to the same; and that I take this obligation freely, without any " +
//                "mental reservation of purpose of evasion, so help me Mother Earth and Father Sky."
        return Output.say(toAnswer);

    }

    @Override
    public State getNextState() {

        getContext().ACTIVE_INTERLOCUTOR_UPDATER.updateValue(person);

        if (alreadyAsked.size() != toAskCounter) {
            return getTransition("loop");
        }
        String color = person.getUzupisInfo().get(COLOR);
        String plant = person.getUzupisInfo().get(PLANT);
        String animal = person.getUzupisInfo().get(ANIMAL);
        String word = person.getUzupisInfo().get(WORD);
        String name = person.getName();
        try {
            ProcessBuilder pb = new ProcessBuilder("python", CertificatesGeneratorScript, "0", color, plant, animal, word, name, "2018");
            pb.start();
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
        return getTransition("next");

    }

    @Override
    protected Set<String> getRequiredParameterNames() {
        return newSet(QAFILEPATH, CERTIFICATESGENERATOR);
    }

}
