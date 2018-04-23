package roboy.context;

import java.util.Map;

/**
 * Maintains a map containing many values.
 * These values are accessible over the getLastNValues method, in addition to AbstractValue methods.
 */
public interface AbstractValueHistory<K, V> extends AbstractValue<V> {
    /**
     * Return the n newest values in the history.
     */
    Map<K, V> getLastNValues(int n);

    /**
     * When value count reaches maxLimit, adding a new value deletes the oldest.
     * Override to change threshold.
     */
    default int getMaxLimit() {
        return 50;
    }

    /**
     * Returns the total amount of updateValue() calls made on this history.
     */
    int getNumberOfValuesSinceStart();

    /**
     * Returns if object is present in this history.
     */
    boolean contains(V value);

    /**
     * Empties the current history.
     */
    boolean purgeHistory();
}
