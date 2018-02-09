package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Map;
import java.util.Observer;

/**
 * The collection of values, split into valueHistories (H) and single values (V).
 */
public class ValueAccessManager<H extends ContextValueInterface, V extends ContextValueInterface> {

    protected ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories;
    protected ImmutableClassToInstanceMap<AbstractValue> values;

    protected <T> T getLastValue(ContextValueInterface attribute) {
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

    protected int valuesAddedSinceStart(H attribute) {
        return valueHistories.get(attribute.getClassType()).valuesAddedSinceStart();
    }
}
