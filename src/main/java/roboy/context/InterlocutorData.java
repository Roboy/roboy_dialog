package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.visionContext.FaceCoordinates;
import roboy.context.visionContext.FaceCoordinatesUpdater;

import java.util.ArrayList;

/**
 * An example for a ContextObject storing information about the interlocutor.
 */

public class InterlocutorData extends ContextObject<InterlocutorData.FaceAttribute> {
    private final ArrayList updatePolicies;

    /**
     * The default constructor of InterlocutorData starts up attributes and updaters.
     */
    public InterlocutorData() {
        // Initialize all attributes centrally.
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        attributes = new ImmutableClassToInstanceMap.Builder<AttributeHistory>()
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        // ContextObject also initializes the updaters for its attributes, if these exist.
        // Here, we have an asynchronous updater for face coordinates.
        updatePolicies = new ArrayList<UpdatePolicy>();
        updatePolicies.add(new FaceCoordinatesUpdater(faceCoordinates));
    }

    /**
     *  Enum of all attributes available from InterlocutorFace and their data types.
     */
    public enum FaceAttribute implements ContextObjectAttributeList {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        FaceAttribute(Class attribute, Class value) {
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
