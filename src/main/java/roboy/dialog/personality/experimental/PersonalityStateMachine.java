package roboy.dialog.personality.experimental;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import roboy.dialog.action.Action;
import roboy.dialog.personality.experimental.helpers.StateFactory;
import roboy.linguistics.sentenceanalysis.Interpretation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State machine for the personality.
 * WORK IN PROGRESS
 *
 */
public abstract class PersonalityStateMachine {

    // maps string identifiers to state objects ("Greeting" -> {GreetingState})
    // allows to have multiple instances of the same state class with different identifiers ("Greeting2" -> {GreetingState})
    private HashMap<String, AbstractState> identifierToState;

    private AbstractState activeState;

    public PersonalityStateMachine() {
        identifierToState = new HashMap<>();
        activeState = null;

    }


    public final void loadStateMachine(File f) throws FileNotFoundException {

        identifierToState.clear();
        activeState = null;

        //System.out.println("Reading from file: " + f);

        JsonParser parser = new JsonParser();
        JsonObject personalityJson = parser.parse(new FileReader(f)).getAsJsonObject();

        //System.out.println("jsonObject: " + personalityJson);

        JsonElement initialStateJson = personalityJson.get("initialState");
        if (initialStateJson == null) {
            System.out.printf("initial state not defined!");
            return;
        }
        String initialStateStr = initialStateJson.getAsString();


        JsonElement statesJson = personalityJson.get("states");
        if (statesJson == null) {
            System.out.printf("states not defined!");
            return;
        }
        JsonArray states = statesJson.getAsJsonArray();

        // for each state: create an object of the correct type
        // and add it to the hash map
        for (JsonElement state : states) {
            JsonObject s = state.getAsJsonObject();

            String identifier = s.get("identifier").getAsString();
            String implementation = s.get("implementation").getAsString();

            AbstractState object = StateFactory.getByClassName(implementation, identifier);
            if (object != null) {
                identifierToState.put(identifier, object);
            }
        }


        // now all states were converted into objects
        // set initial state
        activeState = identifierToState.get(initialStateStr);


        // set fallbacks and transitions (if defined)
        for (JsonElement state : states) {
            JsonObject s = state.getAsJsonObject();

            String identifier = s.get("identifier").getAsString();
            AbstractState thisState = identifierToState.get(identifier);


            // check if fallback is defined
            JsonElement fallbackEntry = s.get("fallback");
            if (fallbackEntry != null && !fallbackEntry.isJsonNull()) {
                String fallbackIdentifier = fallbackEntry.getAsString();
                if (fallbackIdentifier != null) {
                    AbstractState fallbackState = identifierToState.get(fallbackIdentifier);
                    thisState.setFallback(fallbackState);
                }
            }


            // set the transitions
            JsonObject transitions = s.getAsJsonObject("transitions");
            if (transitions != null && !transitions.isJsonNull()) {

                for (Map.Entry<String,JsonElement> entry : transitions.entrySet()) {
                    String transitionName = entry.getKey();
                    String transitionTarget = entry.getValue().getAsString();

                    AbstractState transitionState = identifierToState.get(transitionTarget);

                    if (transitionState != null) {
                        thisState.setTransition(transitionName, transitionState);
                    }

                }
            }

        }


    }

    public final void saveStateMachine(File f) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public abstract List<Action> answer(Interpretation input);


    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("###################################\n");
        s.append("Personality State Machine\n");
        s.append("###################################\n");

        s.append(">> Current state:\n");
        s.append(activeState).append("\n");

        s.append(">> All states:\n");
        for (AbstractState state : identifierToState.values()) {
            s.append(state);
        }

        s.append("###################################\n");

        return s.toString();
    }

}
