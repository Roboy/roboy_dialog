package roboy.context;

import roboy.context.dataTypes.DataType;

import java.util.HashMap;
/**
 * Simplistic HashMap history with unique Integer keys.
 */
public class SimpleAttributeHistory<K extends Integer, V extends DataType> implements AttributeHistory<K,V> {
    /**
     * This counter tracks the number of items in the History, indices still start from 0.
     * Reading is allowed without synchronization, modifications only through generateKey().
     */
    protected int counter;
    private HashMap<K,V> data;

    public SimpleAttributeHistory() {
        data = new HashMap<>();
        counter = 0;
    }

    /**
     * @return The last element added to this History.
     */
    @Override
    public V getLastValue() {
        if (counter == 0) {
            return null;
        } else {
            return getValue((K) new Integer(counter-1));
        }
    }

    /**
     * In a History, only getValue() and storeValue() directly access the HashMap data.
     * Setting these methods to be synchronous avoids concurrency issues.
     * @param key The Integer-valued key of the object in history.
     * @return  A DataType object corresponding to the key, or <code>null</code> if not found.
     */
    @Override
    public synchronized V getValue(K key) {
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }

    /**
     * Get a copy of the last n entries added to the History.
     * Less values may be added if there are not enough values in this History.
     * In case of no values, an empty array is returned.
     * @param n The number of instances to retrieve.
     * @return A hashmap of n last values added to the History.
     */
    @Override
    public HashMap<K, V> getLastNValues(int n) {
        Integer limit = Math.min(n, counter);
        HashMap map = new HashMap();
        while(limit-- > 0) {
            map.put(limit, getValue((K) limit));
        }
        return map;
    }

    /**
     * Puts a value into History and returns the Integer key assigned to it.
     * @param value The DataType value to be added.
     * @return The key of the newly added value.
     */
    @Override
    public synchronized K storeValue(V value) {
        Integer key = generateKey();
        data.put((K) key, value);
        return (K) key;
    }

    /**
     * Generates a key that is unique to the history through incrementing an internal counter.
     * (Note: the uniqueness constraint is only satisfied if access is synchronous.)
     * @return A key unique to this History instance.
     */
    protected synchronized int generateKey() {
        return ++counter;
    };

}
