package roboy.dialog.states.definitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.Context;
import roboy.context.contextObjects.IntentValue;
import roboy.memory.Neo4jProperty;
import roboy.memory.nodes.Roboy;
import roboy.util.RandomList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for Hannover Messe 2018 states.
 * Extends the State class of the dialog state system.
 * Expo dialog states that require probabilistic transitioning should extend this class.
 * Uses uniform distribution.
 *
 * Extends the State class with:
 * - getTransitionRandomly - chooses a named transition randomly from the transition names and intent names.
 * - chooseIntentAttribute - chooses the attribute of the intent randomly, so that it is not similar to the last ones.
 * - lastNIntentsContainAttribute - checks if the last N IntentValues of the IntentsHistory contain the given attribute.
 *
 */
public abstract class ExpoState extends State {

    /**
     * Create a state object with given identifier (state name) and parameters.
     * <p>
     * The parameters should contain a reference to a state machine for later use.
     * The state will not automatically add itself to the state machine.
     *
     * @param stateIdentifier identifier (name) of this state
     * @param params          parameters for this state, should contain a reference to a state machine
     */
    public ExpoState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    private final Logger LOGGER = LogManager.getLogger();

    /**
     * Chooses a named transition randomly. Gets transition names and intent names.
     * Upon choosing the transition checks if the corresponding intent is a valid Neo4jProperty.
     * If so, requests the attribute based on the property and saves property and attribute in the IntentsHistory.
     * If the attribute is unrecoverable, returns the current state. Otherwise, returns the chosen state.
     * When the dice hits a bigger number, returns the current state.
     *
     * @param transitionNames
     * @param intentNames
     * @param intentsHistoryId
     * @return transitionNames[i] -> State
     * @throws IllegalArgumentException
     */
    public final State getTransitionRandomly(String[] transitionNames, String[] intentNames, String intentsHistoryId)
            throws IllegalArgumentException {

        LOGGER.debug("Try to choose a random transition");
        int dice = (int) (transitionNames.length * Math.random() + 1);
        if (transitionNames.length != intentNames.length) {
            throw new IllegalArgumentException("The arrays have different length");
        }
        if (dice < transitionNames.length) {
            for (Neo4jProperty property : Neo4jProperty.values()) {
                if (property.name().equals(intentNames[dice])) {
                    String attribute = chooseIntentAttribute(property, 2);
                    if (!attribute.equals("")) {
                        IntentValue historyValue = new IntentValue(intentsHistoryId, property, attribute);
                        LOGGER.debug("The intent history value: " + historyValue);
                        getContext().DIALOG_INTENTS_UPDATER.updateValue(historyValue);
                        LOGGER.info(transitionNames[dice] + " transition");
                        return getTransition(transitionNames[dice]);
                    } else {
                        LOGGER.info("Stay in the current state");
                        return this;
                    }
                }
            }
            LOGGER.info(transitionNames[dice] + "SELECTED_ROBOY_QA transition");
            return getTransition(transitionNames[dice]);
        } else {
            LOGGER.info("Stay in the current state");
            return this;
        }
    }

    /**
     * Chooses the attribute of the intent randomly, so that it is not similar to the last n intents.
     *
     * @param predicate
     * @param evaluateLastN
     * @return String containing the chosen value
     */
    private String chooseIntentAttribute(Neo4jProperty predicate, int evaluateLastN) {
        LOGGER.debug("Trying to choose the intent attribute");
        Roboy roboy = new Roboy(getMemory());
        String attribute = "";
        HashMap<Neo4jProperty, Object> properties = roboy.getProperties();
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            if (properties.containsKey(predicate)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(predicate).toString().split(",")));
                int count = 0;
                do {
                    attribute = retrievedResult.getRandomElement();
                    count++;
                } while (lastNIntentsContainAttribute(attribute, evaluateLastN) && count < retrievedResult.size());
            }
        }
        LOGGER.debug("The chosen attribute: " + attribute);
        return attribute;
    }

    /**
     * Checks if the last N IntentValues of the IntentsHistory contain the given attribute.
     *
     * @param attribute
     * @param n
     * @return true if contains otherwise false
     */
    private boolean lastNIntentsContainAttribute(String attribute, int n) {
        Map<Integer, IntentValue> lastIntentValues = getContext().DIALOG_INTENTS.getLastNValues(n);

        for (IntentValue value : lastIntentValues.values()) {
            if (value.getAttribute() != null) {
                if (value.getAttribute().equals(attribute)) {
                    return true;
                }
            }
        }
        return false;
    }
}
