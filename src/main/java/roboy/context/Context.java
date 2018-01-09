package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.visionContext.FaceCoordinates;
import roboy.context.visionContext.FaceCoordinatesUpdater;

import java.util.ArrayList;

/**
 * Singleton class serving as an interface to access all context objects.
 * Takes care of correct initialization of attribute histories and updaters.
 *
 * Simple attributes (which implement the AttributeHistory interface) are
 * handled through the inherited AttributeManager methods.
 */
public class Context extends AttributeManager<Context.Attribute>{
    private static Context context;
    private final ArrayList updatePolicies = new ArrayList<UpdatePolicy>();

    private Context() {
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        attributes = new ImmutableClassToInstanceMap.Builder<AttributeHistory>()
                // Initialize all attributes centrally right here.
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        // Also initialize the updaters for attributes.
        updatePolicies.add(new FaceCoordinatesUpdater(faceCoordinates));
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
    public enum Attribute implements AttributeInterface {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        Attribute(Class attribute, Class value) {
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
}
