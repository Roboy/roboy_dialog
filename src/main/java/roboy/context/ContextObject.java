package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.DataType;

/**
 * The generic situation object class.
 */
public abstract class ContextObject<A extends ContextObjectAttributeList> {

    protected ImmutableClassToInstanceMap<Attribute> attributes;

    public abstract <T extends DataType> T getLastAttributeValue(A attribute);
}
