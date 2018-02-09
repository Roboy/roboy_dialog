package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.contextObjects.*;
import roboy.memory.nodes.Interlocutor;
import roboy.ros.RosMainNode;
import roboy_communication_cognition.DirectionVector;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Observer;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 * <p>
 * Queries to values are handled through the inherited ValueAccessManager methods.
 * <p>
 * For usage examples, check out ContextTest.java
 */
public class Context extends ValueAccessManager<Context.ValueHistories, Context.Values> {
    private static Context context;
    private static final Object initializationLock = new Object();

    protected ImmutableClassToInstanceMap<InternalUpdater> internalUpdaters;
    protected ImmutableClassToInstanceMap<ExternalUpdater> externalUpdaters;
    protected ImmutableClassToInstanceMap<Observer> observers;

    private RosMainNode node;

    /**
     * Builds the class to instance maps.
     */
    private Context() {
        values = ContextObjectFactory.buildValueInstanceMap(Values.values());
        valueHistories = ContextObjectFactory.buildValueInstanceMap(ValueHistories.values());
        // Updaters need a target, therefore different initialization.
        internalUpdaters = ContextObjectFactory.buildUpdaterInstanceMap
                (InternalUpdaters.values(), values, valueHistories, node);
        // Observers need to be added to the Observable instance, therefore different initialization.
        observers = ContextObjectFactory.buildObserverInstanceMap(Observers.values(), values, valueHistories);
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
     * Starts up the external updaters (which need a ROS main node).
     * @param ros
     */
    public void initializeROS(RosMainNode ros) {
        Context ctx = Context.getInstance();
        synchronized (initializationLock) {
            // Initialize if needed.
            if(ctx.externalUpdaters == null && ros != null) {
                ctx.node = ros;
                ctx.externalUpdaters = ContextObjectFactory.buildUpdaterInstanceMap(ExternalUpdaters.values(),
                        values, valueHistories, node);
            }
        }
    }

    /**
     * ADD NEW VALUES HERE.
     * This is the interface over which Context values can be queried.
     * Add your Value implementation class and its return type below.
     * Context will take care of initialization.
     * Query values over the enum name.
     */
    public enum Values implements ContextValueInterface {
        // NEW DEFINITIONS GO HERE.
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class),
        ACTIVE_INTERLOCUTOR(ActiveInterlocutor.class, Interlocutor.class);


        final Class classType;
        final Class returnType;

        /**
         * Get the last element added to the corresponding Value instance.
         * @param <T> The return type of the Value.
         * @return
         */
        public <T> T getValue() {
            return Context.getInstance().getValue(this);
        }

        /* Utility methods. */
        Values(Class attribute, Class value) {
            this.classType = attribute;
            this.returnType = value;
        }
        public Class getClassType() {
            return this.classType;
        }
        public Class getReturnType() {
            return this.returnType;
        }
    }

    /**
     * ADD NEW VALUE HISTORIES HERE.
     * This is the interface over which Context value histories can be queried.
     * Add your ValueHistory implementation class and its return type below.
     * Context will take care of initialization.
     * Query values over the enum name.
     */
    public enum ValueHistories implements ContextValueInterface {
        // NEW DEFINITIONS GO HERE.
        DIALOG_TOPICS(DialogTopics.class, String.class),
        AUDIO_ANGLES(AudioDirection.class, DirectionVector.class),
        ROS_TEST(ROSTest.class, String.class);

        final Class classType;
        final Class returnType;

        /**
         * Get a map of elements most recently added to the history.
         * @param n Amount of values to retrieve.
         * @param <T> Return type of the history.
         * @return Map of n most recent values, ordered through Integer keys going upwards from 0 (smaller = older).
         */
        public <T> Map<Integer, T> getNLastValues(int n) {
            return Context.getInstance().getNLastValues(this, n);
        }

        /**
         * Get the last element added to the corresponding ValueHistory instance.
         * @param <T> The return type of the ValueHistory.
         */
        public <T> T getLastValue() {
            return Context.getInstance().getLastValue(this);
        }

        public int valuesAddedSinceStart() {
            return Context.getInstance().valuesAddedSinceStart(this);
        }

        /** ValueHistory enum utility methods. */
        ValueHistories(Class<? extends AbstractValueHistory> attribute, Class dataType) {
            this.classType = attribute;
            this.returnType = dataType;
        }
        public Class getClassType() {
            return this.classType;
        }
        public Class getReturnType() {
            return this.returnType;
        }
    }

    /**
     * ADD NEW INTERNAL UPDATERS HERE.
     * These updaters can be called from DM to add new elements to values or histories.
     * Add your InternalUpdater implementation class, the target class, and its data type below.
     * Context will take care of initialization.
     * Add values over <enum name>.updateValue().
     */
    public enum InternalUpdaters implements ContextUpdaterInterface {
        // NEW DEFINITIONS GO HERE.
        DIALOG_TOPICS_UPDATER(DialogTopicsUpdater.class, DialogTopics.class, String.class),
        ACTIVE_INTERLOCUTOR_UPDATER(ActiveInterlocutorUpdater.class, ActiveInterlocutor.class, Interlocutor.class);

        final Class classType;
        final Class targetType;
        final Class targetValueType;

        /**
         * Directly update an attribute.
         * @param value   Data to put into the Value or ValueHistory object.
         */
        public <V> void updateValue(V value) {
            Class type = this.targetValueType;
            Context.getInstance().internalUpdaters.get(this.getClassType()).putValue(type.cast(value));
        }

        /* Utility methods. */
        InternalUpdaters(Class attribute, Class targetType, Class targetValueType) {
            this.classType = attribute;
            this.targetType = targetType;
            this.targetValueType = targetValueType;
        }
        public Class<? extends AbstractValue> getTargetType() {
            return this.targetType;
        }
        public Class<? extends AbstractValue> getClassType() {
            return this.classType;
        }
        public Class<? extends AbstractValue> getReturnType() {
            return this.targetValueType;
        }

    }
    /**
     * ADD NEW EXTERNAL UPDATERS HERE.
     * These updaters will be initialized and left to run independently.
     * Add your ExternalUpdater implementation class, the target class, and its data type below.
     */
    public enum ExternalUpdaters implements ContextUpdaterInterface {
        // NEW DEFINITIONS GO HERE.
        FACE_COORDINATES_UPDATER(FaceCoordinatesUpdater.class, FaceCoordinates.class, CoordinateSet.class),
        AUDIO_ANGLES_UPDATER(AudioDirectionUpdater.class, AudioDirection.class, DirectionVector.class),
        ROS_TEST_UPDATER(ROSTestUpdater.class, ROSTest.class, String.class);

        final Class classType;
        final Class targetType;
        final Class targetValueType;

        /* Utility methods. */
        ExternalUpdaters(Class attribute, Class targetType, Class targetValueType) {
            this.classType = attribute;
            this.targetType = targetType;
            this.targetValueType = targetValueType;
        }
        public Class<? extends AbstractValue> getClassType() {
            return this.classType;
        }
        public Class<? extends AbstractValue> getTargetType() {
            return this.targetType;
        }
        public Class<? extends AbstractValue> getReturnType() {
            return this.targetValueType;
        }
    }

    /**
     * ADD NEW OBSERVERS FOR CONTEXT OBJECTS HERE.
     * These observers will be initialized and left to run independently.
     */
    public enum Observers implements ContextObserverInterface {
        FACE_COORDINATES_OBSERVER(FaceCoordinatesObserver.class, FaceCoordinates.class);

        final Class classType;
        final Class targetType;

        Observers(Class classType, Class targetType) {
            this.classType = classType;
            this.targetType = targetType;
        }

        public Class<? extends AbstractValue> getClassType() {
            return this.classType;
        }
        public Class<? extends AbstractValue> getTargetType() {
            return this.targetType;
        }
    }
}
