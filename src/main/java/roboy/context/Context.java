package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * Singleton class to access all context objects. Takes care of initialization.
 */
public class Context {
    private static ImmutableClassToInstanceMap<ContextObject> situationObjects;
    private static Context context;

    public enum ContextObjectIdentifier {
        FACE(InterlocutorData.class);

        public final Class className;

        ContextObjectIdentifier(Class className) {
            this.className = className;
        }
    }

    private Context() {
        situationObjects = new ImmutableClassToInstanceMap.Builder<ContextObject>()
                .put(InterlocutorData.class, new InterlocutorData())
                .build();
    }

    public static Context getInstance() {
        if(context == null) {
            context = new Context();
        }
        return context;
    }

    public ContextObject getContextObject(ContextObjectIdentifier identifier) {
        return situationObjects.get(identifier.className);
    }
}
