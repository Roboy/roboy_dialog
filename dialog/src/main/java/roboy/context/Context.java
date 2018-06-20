package roboy.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.context.contextObjects.*;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy.util.ConfigManager;
import roboy_communication_cognition.DirectionVector;

import java.util.ArrayList;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization.
 * For usage examples, check out ContextTest.java
 */
public class Context {

    Logger LOGGER = LogManager.getLogger();
    private static final Object initializationLock = new Object();

    /* VALUES */
    public final ValueInterface<FaceCoordinates, CoordinateSet> FACE_COORDINATES =
            new ValueInterface<>(new FaceCoordinates());

    public final ValueInterface<ActiveInterlocutor, Interlocutor> ACTIVE_INTERLOCUTOR =
            new ValueInterface<>(new ActiveInterlocutor());

    /* VALUE HISTORIES */
    public final HistoryInterface<DialogTopics, Integer, String> DIALOG_TOPICS =
            new HistoryInterface<>(new DialogTopics());

    public final HistoryInterface<DialogIntents, Integer, IntentValue> DIALOG_INTENTS =
            new HistoryInterface<>(new DialogIntents());

    public final HistoryInterface<AudioDirection, Integer, DirectionVector> AUDIO_ANGLES =
            new HistoryInterface<>(new AudioDirection());

    public final HistoryInterface<ROSTest, Integer, String> ROS_TEST =
            new HistoryInterface<>(new ROSTest());


    public final HistoryInterface<ValueHistory<Integer>, Integer, Integer> OTHER_Q =
            new HistoryInterface<>(new ValueHistory<Integer>());

    /* GUI */
    private static final ArrayList guiValues = new ArrayList();
    private static final ArrayList guiHistories = new ArrayList();

    /* INTERNAL UPDATERS */
    public final DialogTopicsUpdater DIALOG_TOPICS_UPDATER;
    public final DialogIntentsUpdater DIALOG_INTENTS_UPDATER;
    public final ActiveInterlocutorUpdater ACTIVE_INTERLOCUTOR_UPDATER;
    public final OtherQuestionsUpdater OTHER_QUESTIONS_UPDATER;

    /* EXTERNAL UPDATERS */
    private boolean rosInitialized = false;
    private AudioDirectionUpdater AUDIO_ANGLES_UPDATER;
    private ROSTestUpdater ROS_TEST_UPDATER;

    /* OBSERVERS */
    private final FaceCoordinatesObserver FACE_COORDINATES_OBSERVER;


    // By calling getInstance, Context initializes its updaters and observers.
    private static Context context = Context.getInstance();

    /**
     * Builds the class to instance maps.
     */
    private Context() {
        /* INTERNAL UPDATER INITIALIZATION */
        DIALOG_TOPICS_UPDATER = new DialogTopicsUpdater(DIALOG_TOPICS.valueHistory);
        DIALOG_INTENTS_UPDATER = new DialogIntentsUpdater(DIALOG_INTENTS.valueHistory);
        ACTIVE_INTERLOCUTOR_UPDATER = new ActiveInterlocutorUpdater(ACTIVE_INTERLOCUTOR.value);
        OTHER_QUESTIONS_UPDATER = new OtherQuestionsUpdater(OTHER_Q.valueHistory);

        /* OBSERVER INITIALIZATION */
        FACE_COORDINATES_OBSERVER = new FaceCoordinatesObserver();
        FACE_COORDINATES.value.addObserver(FACE_COORDINATES_OBSERVER);

        /* GUI INITIALIZATION */
        addToGUI(FACE_COORDINATES,
                ACTIVE_INTERLOCUTOR,
                DIALOG_TOPICS,
                DIALOG_INTENTS,
                AUDIO_ANGLES,
                ROS_TEST);

        if(ConfigManager.CONTEXT_GUI_ENABLED) {
            final Runnable gui = () -> ContextGUI.run(guiValues, guiHistories);
            Thread t = new Thread(gui);
            t.start();
        }
    }

    /**
     * Starts up the external updaters (which need a ROS main node).
     * @param ros
     */
    public void initializeROS(RosMainNode ros) {
        Context ctx = Context.getInstance();
        synchronized (initializationLock) {
            // Initialize only if not already done.
            if(!rosInitialized) {

                /* EXTERNAL UPDATERS INITIALIZED HERE */
//                AUDIO_ANGLES_UPDATER = new AudioDirectionUpdater(AUDIO_ANGLES.valueHistory, ros);
//                ROS_TEST_UPDATER = new ROSTestUpdater(ROS_TEST.valueHistory, ros);
                // TODO Add a FACE_COORDINATE_UPDATER.
                // Edit the data type and integration tests, once the real data type is used.

                rosInitialized = true;
            }
        }
    }

    /**
     * The access point to Context, including thread-safe Singleton initialization.
     */
    public static Context getInstance() {
        if (context == null) {
            // Extra block instead of synchronizing over entire getInstance method.
            // This way, we do not sync when context was initialized earlier -> better performance.
            synchronized (initializationLock) {
                // Need to check for null again in case some other thread got here before.
                if(context == null) {
                    context = new Context();
                }
            }
        }
        return context;
    }

    private void addToGUI(Object ... elements) {
        for(Object newElement : elements) {
            if (HistoryInterface.class.isAssignableFrom(newElement.getClass())) {
                guiHistories.add(((HistoryInterface) newElement).valueHistory);
            } else if (ValueInterface.class.isAssignableFrom(newElement.getClass())) {
                guiValues.add(((ValueInterface) newElement).value);
            } else {
                LOGGER.warn("Unexpected object was passed to addToGUI: {}", newElement.getClass().getSimpleName());
            }
        }
    }
}
