package roboy.context;

import java.util.HashMap;

/**
 * I don't know what I am doing yet, but I guess context needs a main class and this would be it.
 */
public class Context {
    private static HashMap<SituationObjectIdentifier, SituationObject> situationObjects;

    public enum SituationObjectIdentifier {
        Generic("Generic");

        public String objectType;

        SituationObjectIdentifier(String objectType) {
            this.objectType=objectType;
        }
    }
}
