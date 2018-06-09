package roboy.context;

import java.util.ArrayList;
import java.util.Map;

/**
 * This is the interface over which Context value histories can be queried.
 * Initialize as static field of the Context class.
 * Add your ValueHistory implementation class, its key and return types as generic parameters.
 *
 * @param <I> An implementation of AbstractValueHistory.
 * @param <K> The keys used within the History instance.
 * @param <V> The type of data stored within the History instance.
 */
public class HistoryInterface<I extends AbstractValueHistory<K, V>, K, V> {

    protected I valueHistory;

    protected HistoryInterface(I valueHistory) {
        this.valueHistory = valueHistory;
    }

    I getContextObject() {
        return valueHistory;
    }

    /**
     * Get n elements saved into the corresponding ValueHistory instance (or all elements, if all < n).
     */
    public Map<K, V> getLastNValues(int n) {
        return valueHistory.getLastNValues(n);
    }

    /**
     * Get the last element saved into the corresponding ValueHistory instance.
     */
    public V getLastValue() {
        return valueHistory.getValue();
    }

    /**
     * Get the total nr of times a new value was saved into the corresponding ValueHistory instance.
     * Note: as histories can be limited in size, less elements might be actually stored than the total.
     */
    public int valuesAddedSinceStart() {
        return valueHistory.getNumberOfValuesSinceStart();
    }

    /**
     * Check if the object exists among the valueHistory values
     */
    public boolean contains(V value) {
        return valueHistory.contains(value);
    }

    /**
     * Removes all the valueHistory values
     */
    public boolean purgeHistory() {
        return valueHistory.purgeHistory();
    }
}
