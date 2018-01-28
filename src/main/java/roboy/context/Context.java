package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.contextObjects.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

    private ImmutableClassToInstanceMap<InternalUpdater> internalUpdaters;
    private ImmutableClassToInstanceMap<ExternalUpdater> externalUpdaters;

    private Context() {
        // Build the class to instance map of Values.
        values = buildValueInstanceMap(Values.values());
        valueHistories = buildValueInstanceMap(ValueHistories.values());
        externalUpdaters = buildUpdaterInstanceMap(ExternalUpdaters.values());
        internalUpdaters = buildUpdaterInstanceMap(InternalUpdaters.values());
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
     * ADD NEW VALUE HISTORIES HERE.
     * This is the interface over which Context value histories can be queried.
     * Add your ValueHistory implementation class and its return type below.
     * Context will take care of initialization.
     * Query values over the enum name.
     */
    public enum ValueHistories implements ContextValueInterface {
        // NEW DEFINITIONS GO HERE.
        DIALOG_TOPICS(DialogTopics.class, String.class);

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
     * ADD NEW VALUES HERE.
     * This is the interface over which Context values can be queried.
     * Add your Value implementation class and its return type below.
     * Context will take care of initialization.
     * Query values over the enum name.
     */
    public enum Values implements ContextValueInterface {
        // NEW DEFINITIONS GO HERE.
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        /**
         * Get the last element added to the corresponding Value instance.
         * @param <T> The return type of the Value.
         * @return
         */
        public <T> T getLastValue() {
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
     * ADD NEW INTERNAL UPDATERS HERE.
     * These updaters can be called from DM to add new elements to values or histories.
     * Add your InternalUpdater implementation class, the target class, and its data type below.
     * Context will take care of initialization.
     * Add values over <enum name>.updateValue().
     */
    public enum InternalUpdaters implements ContextUpdaterInterface {
        // NEW DEFINITIONS GO HERE.
        DIALOG_TOPICS_UPDATER(DialogTopicsUpdater.class, DialogTopics.class, String.class);

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
    private enum ExternalUpdaters implements ContextUpdaterInterface {
        // NEW DEFINITIONS GO HERE.
        FACE_COORDINATES_UPDATER(FaceCoordinatesUpdater.class, FaceCoordinates.class, CoordinateSet.class);

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
     * Used to initialize the Values and ValueHistories, returning a ClassToInstance map.
     * For each element in a ContextValueInterface enum, generates an instance of its classType.
     * Then returns the generated instances in a ClassToInstance map.
     */
    private <T extends ContextValueInterface> ImmutableClassToInstanceMap buildValueInstanceMap(T[] enumValueList) {
        ImmutableClassToInstanceMap.Builder valueMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        for(T v : enumValueList) {
            try {
                // Get the class which defines the Value/ValueHistory.
                Class c = v.getClassType();
                // Create an instance and add {(class) -> (instance)} to the map.
                valueMapBuilder = valueMapBuilder.put(c, c.getConstructor().newInstance());
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException |InvocationTargetException e) {
                // Just don't mess around when defining the classes and enums.
                e.printStackTrace();
            }
        }
        return valueMapBuilder.build();
    }

    /**
     * Used to initialize Updaters (external and internal), returning a ClassToInstance map.
     * For each element in a ContextUpdaterInterface enum:
     *  1. Seeks out the instance of its targetType (a Value or ValueHistory class).
     *  2. Generates an instance of the updater's classType, with a reference to the target.
     * Finally, returns the generated Updater instances in a ClassToInstance map.
     */
    private <T extends ContextUpdaterInterface> ImmutableClassToInstanceMap buildUpdaterInstanceMap(T[] enumValueList) {
        ImmutableClassToInstanceMap.Builder updaterMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        // Go over all Updaters defined in the enum.
        for(T updater : enumValueList) {
            Class targetClass = updater.getTargetType();
            // Check the Value list in the Context for a target.
            AbstractValue targetInstance = values.get(targetClass);
            // If not there, check ValueHistories.
            if (targetInstance == null) {
                targetInstance = valueHistories.get(targetClass);
            }
            // Not found? Updater must have been defined wrongly.
            if (targetInstance == null) {
                throw new IllegalArgumentException("The target class "+ targetClass.getName() +" was not initialized!");
            }
            try {
                // Get the Updater class.
                Class updaterType = updater.getClassType();
                // Create an instance of the Updater class, with the target as its constructor parameter.
                updaterMapBuilder.put(updaterType, updaterType.getConstructor(targetClass).newInstance(targetInstance));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // Don't mess around defining Updaters (change constructor access or signature, for example).
                e.printStackTrace();
            }
        }
        return updaterMapBuilder.build();
    }

}
