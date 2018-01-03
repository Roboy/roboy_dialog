package roboy.context;

import roboy.context.visionContext.InterlocutorFace;

import java.util.HashMap;

/**
 * I don't know what I am doing yet, but if context needs a primary class, this would be it.
 */
public class Context {
    private static HashMap<ContextObjectIdentifier, ContextObject> situationObjects;

    public enum ContextObjectIdentifier {
        GENERIC(ContextObject.class),
        FACE(InterlocutorFace.class);

        public Class objectType;

        ContextObjectIdentifier(Class objectType) {
            this.objectType=objectType;
        }
    }
}
