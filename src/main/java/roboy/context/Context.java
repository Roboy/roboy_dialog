package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.dataTypes.DataType;
import roboy.context.memoryContext.InterlocutorNode;
import roboy.context.memoryContext.InterlocutorNodeUpdater;
import roboy.context.visionContext.FaceCoordinates;
import roboy.context.visionContext.FaceCoordinatesUpdater;

import java.util.ArrayList;
import java.util.Map;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 *
 * Simple attributes (which implement the AttributeHistory interface) are
 * handled through the inherited AttributeManager methods.
 */
public class Context extends AttributeManager<Context.HistoryAttributes, Context.ValueAttributes>{
    private static Context context;
    private final ArrayList updatePolicies = new ArrayList<UpdatePolicy>();
    private InterlocutorNode interlocutor;

    private Context() {
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        attributes = new ImmutableClassToInstanceMap.Builder<HistoryAttribute>()
                // Initialize all attributes centrally right here.
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        // Initialize objects.
        interlocutor = new InterlocutorNode();

        // Also initialize the updaters for attributes.
        updatePolicies.add(new FaceCoordinatesUpdater(faceCoordinates, 1));
        updatePolicies.add(new InterlocutorNodeUpdater(interlocutor, 10));
    }

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    /**
     *  Enum of all available attributes.
     *  AttributeManager methods take an Attribute as parameter.
     */
    public enum HistoryAttributes implements AttributeInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        HistoryAttributes(Class attribute, Class value) {
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

    public enum ValueAttributes implements AttributeInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        ValueAttributes(Class attribute, Class value) {
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
}
