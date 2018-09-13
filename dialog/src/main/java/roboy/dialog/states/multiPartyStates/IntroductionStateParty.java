package roboy.dialog.states.multiPartyStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.Triple;
import roboy.linguistics.sentenceanalysis.Interpretation;
import roboy.memory.Neo4jProperty;
import roboy.memory.Neo4jRelationship;
import roboy.memory.nodes.Interlocutor;
import roboy.memory.nodes.Interlocutor.RelationshipAvailability;
import roboy.memory.nodes.Roboy;
import roboy.memory.nodes.MemoryNodeModel;
import roboy.dialog.Segue;
import roboy.util.Agedater;
import roboy.util.QAJsonParser;
import roboy.util.RandomList;
import roboy.io.SpeakerInfo;

import java.util.*;

import static roboy.memory.Neo4jRelationship.*;
import static roboy.memory.Neo4jProperty.*;
import static roboy.memory.nodes.Interlocutor.RelationshipAvailability.*;

public class IntroductionStateParty extends State{

    private QAJsonParser infoValues;

    private final String LEARN_ABOUT_PERSON = "newPerson";

    private final RandomList<String> introPhrases = new RandomList<>("What's your name?");
    private final Logger LOGGER = LogManager.getLogger();

    private State nextState;

    private final String INFO_FILE_PARAMETER_ID = "infoFile";

    private RandomList<Neo4jRelationship> roboyRelatioshipPredicates = new RandomList<>(FROM, MEMBER_OF, LIVE_IN, HAS_HOBBY, FRIEND_OF, CHILD_OF, SIBLING_OF);

    private final RandomList<String> failureResponsePhrases = new RandomList<>("Nice to meet you ");


    public IntroductionStateParty(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
        String infoListPath = params.getParameter(INFO_FILE_PARAMETER_ID);
        LOGGER.info(" -> The infoList path: " + infoListPath);
        infoValues = new QAJsonParser(infoListPath);
    }


    @Override
    public Output act() {
        return Output.say(getIntroPhrase());
    }

    @Override
    public Output react(Interpretation input) {
        return null;
    }

    @Override
    public Output react(ArrayList<Interpretation> input) {

        /*
        // expecting something like "My name is NAME"

        int speakerCount = input.get(0).getSpeakerInfo().getSpeakerCount();
        ArrayList<String> names = new ArrayList<>();

        Map<Integer, Interlocutor> persons = getContext().ACTIVE_INTERLOCUTORS.getValue();

        if(speakerCount>1) {
            //New speaker detected
            //add new interlocutor
            Interlocutor person = new Interlocutor(getMemory());
            // do I need this??
            // person.setProperty(Neo4jProperty.telegram_id, uuid); //what is the string uuid
            persons.put(2, person);
            getContext().ACTIVE_INTERLOCUTORS_UPDATER.updateValue(persons);
        }

        for(int i=0; i<speakerCount; i++){
            String name = getNameFromInput(input.get(i));
            names.add(name);
            if(name==null){
                //TODO handle this smart
                //what is the next state, if we get one name but not two???
            }
            // 2. get interlocutor object from context
            // this also should query memory and do other magic
            persons.get(i).addName(name);
        }


        // 3. update interlocutor in context
        getContext().ACTIVE_INTERLOCUTORS_UPDATER.updateValue(persons);
        String retrievedPersonalFact = "";
        Double segueProbability = 0.0;

        // person is not known
        nextState = getTransition(LEARN_ABOUT_PERSON);
        segueProbability = 0.6;


        String retrievedRoboyFacts = getRoboyFactsPhrase(new Roboy(getMemory()));
        Segue s = new Segue(Segue.SegueType.DISTRACT, segueProbability);
        //TODO how to react to two or more persons persons

        String response = failureResponsePhrases.getRandomElement();
        for(int i=0; i<speakerCount; i++){
            response = response + persons.get(i).getName() + " ";
            if(i!=speakerCount){
                response = response + "and ";
            }
        }

        return Output.say(response + retrievedPersonalFact + retrievedRoboyFacts).setSegue(s);

        */
        return Output.say("Hi");
    }


    @Override
    public State getNextState() {
        // TODO Auto-generated method stub
        return null;
    }

    private String getIntroPhrase() {
        return introPhrases.getRandomElement();
    }

    private String getNameFromInput(Interpretation input) {
        return getInference().inferProperty(name, input);
    }


    private String getRoboyFactsPhrase(Roboy roboy) {
        String result = "";

        // Get some random properties facts
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            HashMap<Neo4jProperty, Object> properties = roboy.getProperties();
            if (properties.containsKey(full_name)) {
                result += " " + String.format(infoValues.getSuccessAnswers(full_name).getRandomElement(), properties.get(full_name));
            }
            if (properties.containsKey(birthdate)) {
                HashMap<String, Integer> ages = new Agedater().determineAge(properties.get(birthdate).toString());
                String retrievedAge = "0 days";
                if (ages.get("years") > 0) {
                    retrievedAge = ages.get("years") + " years";
                } else if (ages.get("months") > 0) {
                    retrievedAge = ages.get("months") + " months";
                } else {
                    retrievedAge = ages.get("days") + " days";
                }
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), retrievedAge);
            } else if (properties.containsKey(age)) {
                result += " " + String.format(infoValues.getSuccessAnswers(age).getRandomElement(), properties.get(age) + " years!");
            }
            if (properties.containsKey(skills)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(skills).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(skills).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(abilities)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(abilities).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(abilities).getRandomElement(), retrievedResult.getRandomElement());
            }
            if (properties.containsKey(future)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(future).toString().split(",")));
                result += " " + String.format(infoValues.getSuccessAnswers(future).getRandomElement(), retrievedResult.getRandomElement());
            }
        }

        if (result.equals("")) {
            result = "I am Roboy 2.0! ";
        }

        // Get a random relationship fact
        Neo4jRelationship predicate = roboyRelatioshipPredicates.getRandomElement();
        MemoryNodeModel node = getMemNodesByIds(roboy.getRelationships(predicate)).getRandomElement();
        if (node != null) {
            String nodeName = "";
            if (node.getProperties().containsKey(full_name) && !node.getProperties().get(full_name).equals("")) {
                nodeName = node.getProperties().get(full_name).toString();
            } else {
                nodeName = node.getProperties().get(name).toString();
            }
            result += " " + String.format(infoValues.getSuccessAnswers(predicate).getRandomElement(), nodeName);
        }

        return result;
    }



}