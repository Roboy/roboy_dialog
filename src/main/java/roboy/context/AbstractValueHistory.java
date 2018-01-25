package roboy.context;

import java.util.Map;

/**
 * ValueHistory maintains a map containing all (current and past) values.
 * These values are accessible over the getLastNValues method.
 */
public interface AbstractValueHistory<K, V> extends AbstractValue<V> {
    Map<K, V> getLastNValues(int n);
}
