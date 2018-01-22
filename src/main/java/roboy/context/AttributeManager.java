package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Map;

/**
 * The collection of values, split into lists (L) and single values (V).
 */
public class AttributeManager<L extends AttributeInterface, V extends AttributeInterface> {

    protected ImmutableClassToInstanceMap<ValueListInterface> lists;
    protected ImmutableClassToInstanceMap<ValueInterface> values;

    protected <T> T getLastValue(L attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(lists.get(attribute.getClassType()).getLastValue());
    }

    protected <T> T getValue(L attribute, Integer key) {
        Class<T> type = attribute.getReturnType();
        return type.cast(lists.get(attribute.getClassType()).getValue(key));
    }

    protected <K, T> Map<K, T> getNLastValues(L attribute, int n) {
        Class<T> type = attribute.getReturnType();
        return (Map<K,T>) lists.get(attribute.getClassType()).getLastNValues(n);
    }

    protected <T> T getValue(V attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(values.get(attribute.getClassType()).getValue());
    }
}
