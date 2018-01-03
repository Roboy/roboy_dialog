package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

/**
 * The generic situation object class.
 */
public abstract class ContextObject {
    protected ImmutableClassToInstanceMap<Attribute> attributes;
}
