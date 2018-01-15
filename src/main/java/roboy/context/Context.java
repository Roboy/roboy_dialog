package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.dataTypes.DataType;
import roboy.context.dataTypes.Topic;
import roboy.context.dialogContext.DialogTopics;
import roboy.context.dialogContext.DialogTopicsUpdater;
import roboy.context.memoryContext.InterlocutorNode;
import roboy.context.memoryContext.InterlocutorNodeUpdater;
import roboy.context.visionContext.FaceCoordinates;
import roboy.context.visionContext.FaceCoordinatesUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 *
 * Simple attributes (which implement the AttributeHistory interface) are
 * handled through the inherited AttributeManager methods.
 *
 * For usage examples, check out ContextTest.java
 */
public class Context extends AttributeManager<Context.HistoryAttributes, Context.ValueAttributes>{
    private static Context context;
    private final ArrayList asyncUpdatePolicies = new ArrayList<AsyncUpdatePolicy>();
    public final HashMap directUpdatePolicies = new HashMap<Class, DirectUpdatePolicy>();
    private InterlocutorNode interlocutor;

    private Context() {
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        DialogTopics dialogTopics = new DialogTopics();

        values = new ImmutableClassToInstanceMap.Builder<ValueAttribute>()
                // Initialize all ValueAttributes centrally right here.
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        attributes = new ImmutableClassToInstanceMap.Builder<HistoryAttribute>()
                // Initialize all ValueAttributes centrally right here.
                .put(DialogTopics.class, dialogTopics)
                .build();

        // Initialize objects.
        interlocutor = new InterlocutorNode();

        // Also initialize the updaters for attributes.
        asyncUpdatePolicies.add(new FaceCoordinatesUpdater(faceCoordinates, 1));
        asyncUpdatePolicies.add(new InterlocutorNodeUpdater(interlocutor, 10));

        directUpdatePolicies.put(DialogTopics.class, new DialogTopicsUpdater(dialogTopics));
    }

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    /**
     *  A listing of available attributes with a history of values.
     *  AttributeManager methods take an Attribute as parameter.
     */
    public enum HistoryAttributes implements AttributeInterface {
        DIALOG_TOPICS(DialogTopics.class, Topic.class);

        final Class classType;
        final Class returnType;

        HistoryAttributes(Class<? extends HistoryAttribute> attribute, Class<?extends DataType> value) {
            this.classType = attribute;
            this.returnType = value;
        }

        public Class getClassType() {
            return this.classType;
        }

        public Class getReturnType() {
            return this.returnType;
        }

        public <T extends DataType> T getLastValue() {
            return Context.getInstance().getLastAttributeValue(this);
        }

        public <T extends DataType> Map<Integer, T> getNLastValues(int n) {
            return Context.getInstance().getNLastValues(this, n);
        }

        public <T extends DataType> T getValue(int key) {
            return Context.getInstance().getAttributeValue(this, key);
        }
    }

    /**
     * A listing of available single-value attributes.
     */
    public enum ValueAttributes implements AttributeInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        ValueAttributes(Class<? extends ValueAttribute> attribute, Class<?extends DataType> value) {
            this.classType = attribute;
            this.returnType = value;
        }

        public Class getClassType() {
            return this.classType;
        }

        public Class getReturnType() {
            return this.returnType;
        }

        public <T extends DataType> T getLastValue() {
            return Context.getInstance().getValue(this);
        }
    }

    /**
     * A listing of available updaters, with their target class specified under classType.
     */
    public enum Updaters {
        DIALOG_TOPICS_UPDATER(DialogTopics.class);
        final Class classType;

        Updaters(Class attribute) {
            this.classType = attribute;
        }
    }

    /**
     * Get the updater to directly add values to an attribute.
     * @param updater The name of the updater.
     * @return An updatePolicy offering the putValue() method.
     */
    public DirectUpdatePolicy getUpdater(Updaters updater) {
        return (DirectUpdatePolicy) directUpdatePolicies.get(updater.classType);
    }
}
