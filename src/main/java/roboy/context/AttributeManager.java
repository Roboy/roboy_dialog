package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.context.dataTypes.DataType;

import java.util.Map;

/**
 * The collection of attributes, split into attributes with a history (A) and single-value attributes (B).
 */
public class AttributeManager<A extends AttributeInterface, B extends AttributeInterface> {

    protected ImmutableClassToInstanceMap<HistoryAttribute> attributes;
    protected ImmutableClassToInstanceMap<ValueAttribute> values;

    protected <T extends DataType> T getLastAttributeValue(A attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(attributes.get(attribute.getClassType()).getLastValue());
    }

    protected <T extends DataType> T getAttributeValue(A attribute, Integer key) {
        Class<T> type = attribute.getReturnType();
        return type.cast(attributes.get(attribute.getClassType()).getValue(key));
    }

    protected <T extends DataType> Map<Integer, T> getNLastValues(A attribute, int n) {
        Class<T> type = attribute.getReturnType();
        return (Map<Integer,T>) attributes.get(attribute.getClassType()).getLastNValues(n);
    }

    protected <T extends DataType> T getValue(B attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(values.get(attribute.getClassType()).getValue());
    }
}
