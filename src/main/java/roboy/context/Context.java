package roboy.context;

import roboy.context.contextObjects.*;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy_communication_cognition.DirectionVector;

import java.util.ArrayList;
import java.util.Map;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization.
 * <p>
 * Queries to values are handled through the inherited ValueAccessManager methods.
 * <p>
 * For usage examples, check out ContextTest.java
 */
public class Context {
    private static final Object initializationLock = new Object();

    /* VALUES INITIALIZED HERE */
    public static final ValueInterface<FaceCoordinates, CoordinateSet> FACE_COORDINATES =
            new ValueInterface<>(new FaceCoordinates());

    // An example of defining a Value without previously defining a new class. Works like FACE_COORDINATES.
    //public static final ValueInterface<ObservableValue<CoordinateSet>, CoordinateSet> FACE_COORDINATES_GENERIC =
    //        new ValueInterface<>(new ObservableValue<CoordinateSet>());

    public static final ValueInterface<ActiveInterlocutor, Interlocutor> ACTIVE_INTERLOCUTOR =
            new ValueInterface<>(new ActiveInterlocutor());

    /* VALUE HISTORIES INITIALIZED HERE */
    public static final HistoryInterface<DialogTopics, Integer, String> DIALOG_TOPICS =
            new HistoryInterface<>(new DialogTopics());

    public static final HistoryInterface<AudioDirection, Integer, DirectionVector> AUDIO_ANGLES =
            new HistoryInterface<>(new AudioDirection());

    public static final HistoryInterface<ROSTest, Integer, String> ROS_TEST =
            new HistoryInterface<>(new ROSTest());

    /* INTERNAL UPDATERS DEFINED HERE */
    public final DialogTopicsUpdater DIALOG_TOPICS_UPDATER;
    public final ActiveInterlocutorUpdater ACTIVE_INTERLOCUTOR_UPDATER;

    /* EXTERNAL UPDATERS DEFINED HERE */
    private volatile boolean rosInitialized = false;
    private AudioDirectionUpdater AUDIO_ANGLES_UPDATER;
    private ROSTestUpdater ROS_TEST_UPDATER;

    /* OBSERVERS DEFINED HERE */
    private final FaceCoordinatesObserver FACE_COORDINATES_OBSERVER;

    private RosMainNode node;

    // By calling getInstance, Context initializes its updaters and observers.
    private static Context context = Context.getInstance();

    /**
     * Builds the class to instance maps.
     */
    private Context() {
        /* INTERNAL UPDATERS INITIALIZED HERE */
        DIALOG_TOPICS_UPDATER = new DialogTopicsUpdater(DIALOG_TOPICS.valueHistory);
        ACTIVE_INTERLOCUTOR_UPDATER = new ActiveInterlocutorUpdater(ACTIVE_INTERLOCUTOR.value);

        /* OBSERVERS INITIALIZED AND ADDED HERE */
        FACE_COORDINATES_OBSERVER = new FaceCoordinatesObserver();
        FACE_COORDINATES.value.addObserver(FACE_COORDINATES_OBSERVER);
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
                AUDIO_ANGLES_UPDATER = new AudioDirectionUpdater(AUDIO_ANGLES.valueHistory, ros);
                ROS_TEST_UPDATER = new ROSTestUpdater(ROS_TEST.valueHistory, ros);

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

    /**
     * This is the interface over which Context values can be queried.
     * Initialize as static field of the Context class above.
     * Add your Value implementation class and its return type as generic parameters.
     *
     * For Context-internal usage, the class keeps track of the initialized values with a static list.
     *
     * @param <I> An implementation of AbstractValue, such as the standard Value, ROS or Observable.
     * @param <V> The type of data stored within the Value instance.
     */
    static class ValueInterface<I extends AbstractValue<V>, V> {
        // Keeping track of all the values instantiated over the ValueInterface class.
        static ArrayList<AbstractValue> allValues = new ArrayList<>();

        private I value;

        protected ValueInterface(I value) {
            this.value = value;
            allValues.add(value);
        }

        I getContextObject() {
            return value;
        }

        /**
         * Get the last element saved into the corresponding Value instance.
         */
        public V getValue() {
            return value.getValue();
        }
    }

    /**
     * This is the interface over which Context value histories can be queried.
     * Initialize as static field of the Context class above.
     * Add your ValueHistory implementation class, its key and return types as generic parameters.
     *
     * For Context-internal usage, the class keeps track of the initialized histories with a static list.
     *
     * @param <I> An implementation of AbstractValueHistory.
     * @param <K> The keys used within the History instance.
     * @param <V> The type of data stored within the History instance.
     */
    static class HistoryInterface<I extends AbstractValueHistory<K, V>, K, V> {
        // Keeping track of all the histories instantiated over the HistoryInterface class.
        static ArrayList<AbstractValueHistory> allHistories = new ArrayList<>();

        private I valueHistory;

        protected HistoryInterface (I valueHistory) {
            this.valueHistory = valueHistory;
            allHistories.add(valueHistory);
        }

        I getContextObject() {
            return valueHistory;
        }

        /**
         * Get n elements saved into the corresponding ValueHistory instance (or all elements, if all < n).
         */
        public Map<K, V> getLastNValues(int n) {
            return valueHistory.getLastNValues(n);
        }

        /**
         * Get the last element saved into the corresponding ValueHistory instance.
         */
        public V getLastValue() {
            return valueHistory.getValue();
        }

        /**
         * Get the total nr of times a new value was saved into the corresponding ValueHistory instance.
         * Note: as histories can be limited in size, less elements might be actually stored than the total.
         */
        public int valuesAddedSinceStart() {
            return valueHistory.valuesAddedSinceStart();
        }
    }
}
