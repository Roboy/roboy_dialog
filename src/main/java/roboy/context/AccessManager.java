package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Map;

/**
 * The collection of values, split into valueHistories (H) and single values (V).
 */
class AccessManager<H extends ContextValueInterface<AbstractValueHistory>,
        V extends ContextValueInterface<AbstractValue>> {

    protected ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories;
    protected ImmutableClassToInstanceMap<AbstractValue> values;

    AccessManager(V[] values, H[] valueHistories) {
        this.values = ContextObjectFactory.buildValueInstanceMap(values);
        this.valueHistories = ContextObjectFactory.buildValueInstanceMap(valueHistories);
    }

    <T> T getValue(V attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(values.get(attribute.getClassType()).getValue());
    }

    <T> T getLastValue(H attribute) {
        Class<T> type = attribute.getReturnType();
        return type.cast(valueHistories.get(attribute.getClassType()).getValue());
    }

    <K, T> Map<K, T> getNLastValues(H attribute, int n) {
        return (Map<K, T>) valueHistories.get(attribute.getClassType()).getLastNValues(n);
    }

    int valuesAddedSinceStart(H attribute) {
        return valueHistories.get(attribute.getClassType()).valuesAddedSinceStart();
    }
}
