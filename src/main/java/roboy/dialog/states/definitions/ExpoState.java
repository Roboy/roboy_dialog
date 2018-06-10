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

    public final State getRandomTransition(String[] transitionNames, String[] intentNames, String intentsHistoryId) throws IllegalArgumentException {
        LOGGER.info("Try to choose a random transition");
        int dice = (int) (3 * Math.random() + 1);
        if (transitionNames.length != intentNames.length) {
            throw new IllegalArgumentException("The arrays have different length");
        }
        if (dice < transitionNames.length) {
            for (Neo4jProperty property : Neo4jProperty.values()) {
                if (property.name().equals(intentNames[dice])) {
                    String attribute = chooseIntentAttribute(property);
                    if (!attribute.equals("")) {
                        Context.getInstance().DIALOG_INTENTS_UPDATER.updateValue(new IntentValue(intentsHistoryId, property, attribute));
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

    private String chooseIntentAttribute(Neo4jProperty predicate) {
        LOGGER.info("Trying to choose the intent attribute");
        Roboy roboy = new Roboy(getMemory());
        String attribute = "";
        HashMap<Neo4jProperty, Object> properties = roboy.getProperties();
        if (roboy.getProperties() != null && !roboy.getProperties().isEmpty()) {
            if (properties.containsKey(predicate.type)) {
                RandomList<String> retrievedResult = new RandomList<>(Arrays.asList(properties.get(predicate.type).toString().split(",")));
                int count = 0;
                do {
                    attribute = retrievedResult.getRandomElement();
                    count++;
                } while (lastNIntentsContainAttribute(attribute, 2) && count < retrievedResult.size());
            }
        }
        LOGGER.info("The chosen attribute: " + attribute);
        return attribute;
    }

    private boolean lastNIntentsContainAttribute(String attribute, int n) {
        Map<Integer, IntentValue> lastIntentValues = Context.getInstance().DIALOG_INTENTS.getLastNValues(n);

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
