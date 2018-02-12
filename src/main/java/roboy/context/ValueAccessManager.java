package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The collection of values, split into valueHistories (H) and single values (V).
 */
class ValueAccessManager<H extends ContextValueInterface<AbstractValueHistory>,
        V extends ContextValueInterface<AbstractValue>> {

    protected ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories;
    protected ImmutableClassToInstanceMap<AbstractValue> values;

    final Logger LOGGER = LogManager.getLogger();

    ValueAccessManager(V[] valueSignatures, H[] historySignatures) {
        ImmutableClassToInstanceMap.Builder<AbstractValue> valueMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        for(V value : valueSignatures) {
            AbstractValue instance = ContextObjectFactory.createValue(value);
            if (instance != null) {
                valueMapBuilder.put(value.getClassType(), instance);
                LOGGER.info("Initialized context value for {}", value.name());
            } else {
                LOGGER.error("Could not initialize context value for {}", value.name());
            }
        }
        this.values = valueMapBuilder.build();

        ImmutableClassToInstanceMap.Builder<AbstractValueHistory> historyMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        for(H history : historySignatures) {
            AbstractValueHistory instance = ContextObjectFactory.createHistory(history);
            if (instance != null) {
                historyMapBuilder.put(history.getClassType(), instance);
                LOGGER.info("Initialized context value history for {}", history.name());
            } else {
                LOGGER.error("Could not initialize context value history for {}", history.name());
            }
        }
        this.valueHistories = historyMapBuilder.build();
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
