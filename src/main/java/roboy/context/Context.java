package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.contextObjects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 * <p>
 * Queries to values are handled through the inherited AttributeManager methods.
 * <p>
 * For usage examples, check out ContextTest.java
 */
public class Context extends AttributeManager<Context.ValueLists, Context.Values> {
    private static Context context;

    private final ArrayList<ExternalUpdater> externalUpdaters = new ArrayList<>();
    public final HashMap<Class, InternalUpdater> internalUpdaters = new HashMap<>();

    private Context() {
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        DialogTopics dialogTopics = new DialogTopics();

        values = new ImmutableClassToInstanceMap.Builder<AbstractValue>()
                // Collect all Values centrally right here.
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        valueHistories = new ImmutableClassToInstanceMap.Builder<AbstractValueHistory>()
                // Collect all ValueHistories centrally right here.
                .put(DialogTopics.class, dialogTopics)
                .build();

        // Initialize and store the external updaters.
        externalUpdaters.add(new FaceCoordinatesUpdater(faceCoordinates, 1));
        // Initialize direct updaters with their targets.
        internalUpdaters.put(DialogTopicsUpdater.class, new DialogTopicsUpdater(dialogTopics));
    }

    public static Context getInstance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    /**
     * All available valueHistories of values.
     */
    public enum ValueLists implements ExternalContextInterface {
        DIALOG_TOPICS(DialogTopics.class, String.class);

        final Class classType;
        final Class returnType;

        ValueLists(Class<? extends AbstractValueHistory> attribute, Class dataType) {
            this.classType = attribute;
            this.returnType = dataType;
        }

        public Class getClassType() {
            return this.classType;
        }

        public Class getReturnType() {
            return this.returnType;
        }

        public <T> T getLastValue() {
            return Context.getInstance().getLastValue(this);
        }

        public <K, T> Map<K, T> getNLastValues(int n) {
            return Context.getInstance().getNLastValues(this, n);
        }
    }

    /**
     * All available values.
     */
    public enum Values implements ExternalContextInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        Values(Class<? extends AbstractValue> attribute, Class value) {
            this.classType = attribute;
            this.returnType = value;
        }

        public Class getClassType() {
            return this.classType;
        }

        public Class getReturnType() {
            return this.returnType;
        }

        public <T> T getLastValue() {
            return Context.getInstance().getValue(this);
        }
    }

    /**
     * All available updaters by their class and their target's value type.
     */
    public enum Updaters {
        DIALOG_TOPICS_UPDATER(DialogTopicsUpdater.class, String.class);

        final Class classType;
        final Class targetValueType;

        Updaters(Class attribute, Class valueType) {
            this.classType = attribute;
            this.targetValueType = valueType;
        }
    }

    /**
     * Directly update an attribute.
     *
     * @param updater The name of the Value or ValueHistory object.
     * @param value   Data to put into the Value or ValueHistory object.
     */
    public <V> void updateValue(Updaters updater, V value) {
        // Could throw exception if the value does not match the target data type.
        Class type = updater.targetValueType;
        internalUpdaters.get(updater.classType).putValue(type.cast(value));
    }
}
