package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.DataType;

import java.util.Map;

/**
 * The generic context object.
 */
public abstract class ContextObject<A extends ContextObjectAttributeList> {

    protected ImmutableClassToInstanceMap<AttributeHistory> attributes;

    public <T extends DataType> T getLastAttributeValue(A attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(attributes.get(attribute.getClassType()).getLastValue());
    }

    public <T extends DataType> T getAttributeValue(A attribute, Integer key) {
        Class<T> type = attribute.getReturnType();
        return type.cast(attributes.get(attribute.getClassType()).getValue(key));
    }

    public <T extends DataType> Map<Integer, T> getNLastAttributes(A attribute, int n) {
        Class<T> type = attribute.getReturnType();
        return (Map<Integer,T>) attributes.get(attribute.getClassType()).getLastNValues(n);
    }
}
