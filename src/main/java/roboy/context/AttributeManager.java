package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Map;

/**
 * The collection of values, split into valueHistories (H) and single values (V).
 */
public class AttributeManager<H extends ExternalContextInterface, V extends ExternalContextInterface> {

    protected ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories;
    protected ImmutableClassToInstanceMap<AbstractValue> values;

    protected <T> T getLastValue(ExternalContextInterface attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(valueHistories.get(attribute.getClassType()).getValue());
    }

    protected <K, T> Map<K, T> getNLastValues(H attribute, int n) {
        return (Map<K, T>) valueHistories.get(attribute.getClassType()).getLastNValues(n);
    }

    protected <T> T getValue(V attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(values.get(attribute.getClassType()).getValue());
    }
}
