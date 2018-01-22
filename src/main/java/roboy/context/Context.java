package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.dialogContext.DialogTopics;
import roboy.context.dialogContext.DialogTopicsUpdater;
import roboy.context.visionContext.FaceCoordinates;
import roboy.context.visionContext.FaceCoordinatesUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 *
 * Queries to values are handled through the inherited AttributeManager methods.
 *
 * For usage examples, check out ContextTest.java
 */
public class Context extends AttributeManager<Context.ValueLists, Context.Values>{
    private static Context context;
    private final ArrayList externalUpdaters = new ArrayList<ExternalUpdater>();
    public final HashMap internalValueUpdaters = new HashMap<Class, InternalValueUpdater>();
    public final HashMap internalListUpdaters = new HashMap<Class, InternalListUpdater>();

    private Context() {
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        DialogTopics dialogTopics = new DialogTopics();

        values = new ImmutableClassToInstanceMap.Builder<ValueInterface>()
                // Collect all Values centrally right here.
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        lists = new ImmutableClassToInstanceMap.Builder<ValueListInterface>()
                // Collect all ValueLists centrally right here.
                .put(DialogTopics.class, dialogTopics)
                .build();

        // Also initialize the updaters for lists.
        externalUpdaters.add(new FaceCoordinatesUpdater(faceCoordinates, 1));

        internalListUpdaters.put(DialogTopicsUpdater.class, new DialogTopicsUpdater(dialogTopics));
    }

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    /**
     *  All available lists of values.
     */
    public enum ValueLists implements AttributeInterface {
        DIALOG_TOPICS(DialogTopics.class, String.class);

        final Class classType;
        final Class returnType;

        ValueLists(Class<? extends ValueListInterface> attribute, Class dataType) {
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

        public <T> T getValue(int key) {
            return Context.getInstance().getValue(this, key);
        }
    }

    /**
     * All available values.
     */
    public enum Values implements AttributeInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        Values(Class<? extends ValueInterface> attribute, Class value) {
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
     * All available updaters, with their target class specified under classType.
     */
    public enum ListUpdaters {
        DIALOG_TOPICS_UPDATER(DialogTopicsUpdater.class);
        final Class classType;

        ListUpdaters(Class attribute) {
            this.classType = attribute;
        }
    }

    /**
     * Get the updater to directly add values to an attribute.
     * @param updater The name of the updater.
     * @return An updatePolicy offering the putValue() method.
     */
    public InternalListUpdater getUpdater(ListUpdaters updater) {
        return (InternalListUpdater) internalListUpdaters.get(updater.classType);
    }
}
