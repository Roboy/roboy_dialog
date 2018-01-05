package roboy.context.visionContext;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.*;
import roboy.context.dataTypes.CoordinateSet;
import roboy.context.dataTypes.DataType;

import java.util.ArrayList;

/**
 * An example with vision using information about the interlocutor.
 */

public class InterlocutorFace extends ContextObject<InterlocutorFace.FaceAttribute> {
    private final ArrayList updatePolicies;

    // Enum of all attributes available from InterlocutorFace and their data types.
    public enum FaceAttribute implements ContextObjectAttributeList {
        FACE_COORDINATES(FaceCoordinates.class, CoordinateSet.class);

        final Class classType;
        final Class returnType;

        FaceAttribute(Class attribute, Class value) {
            this.classType = attribute;
            this.returnType = value;
        }
    }

    /**
     * The default constructor of InterlocutorFace starts up attributes and updaters.
     */
    public InterlocutorFace() {
        // Initialize all attributes centrally.
        FaceCoordinates faceCoordinates = new FaceCoordinates();
        attributes = new ImmutableClassToInstanceMap.Builder<Attribute>()
                .put(FaceCoordinates.class, faceCoordinates)
                .build();

        // ContextObject also initializes the updaters for its attributes, if these exist.
        // Here, we have an asynchronous updater for face coordinates.
        updatePolicies = new ArrayList<UpdatePolicy>();
        updatePolicies.add(new FaceCoordinatesUpdater(faceCoordinates));
    }

    /**
     * Returns the last value of the given attribute.
     * The return type of this method is the same as the returnType of FaceAttribute enum.
     */
    public <T extends DataType> T getLastAttributeValue(InterlocutorFace.FaceAttribute attribute) {
        Class<T> type = attribute.returnType;
        return type.cast(attributes.get(attribute.classType).getLatestValue());
    }
}
