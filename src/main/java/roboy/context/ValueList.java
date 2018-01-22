package roboy.context;

import java.util.HashMap;
/**
 * HashMap implementation of a value list with unique Integer keys.
 */
public class ValueList<K extends Integer,V> implements ValueListInterface<K,V> {
    /**
     * This counter tracks the number of values, indices still start from 0.
     * Reading is allowed without synchronization, modifications only through generateKey().
     */
    protected volatile int counter;
    private HashMap<K,V> data;

    public ValueList() {
        data = new HashMap<>();
        counter = 0;
    }

    /**
     * @return The last element added to this list.
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
     * In a ValueList, only getValue() and storeValue() directly access the HashMap data.
     * Setting these methods to be synchronous avoids concurrency issues.
     * @param key The key of the value.
     * @return  The value, or <code>null</code> if not found.
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
     * Get a copy of the last n entries added to the list.
     * Less values may be returned if there are not enough values in this list.
     * In case of no values, an empty array is returned.
     * @param n The number of instances to retrieve.
     * @return A hashmap of n last values added to the list.
     */
    @Override
    public HashMap<K, V> getLastNValues(int n) {
        HashMap map = new HashMap();
        Integer lastToRetrieve = counter - Math.min(n, counter);
        for(Integer i = counter-1; i >= lastToRetrieve; i--) {
            map.put(i-lastToRetrieve, getValue((K) i));
        }
        return map;
    }

    /**
     * Puts a value into the list and returns the key assigned to it.
     * @param value The value to be added.
     * @return The key of the newly added value.
     */
    @Override
    public synchronized K storeValue(V value) {
        Integer key = generateKey();
        data.put((K) key, value);
        return (K) key;
    }

    /**
     * Generates a key that is unique through incrementing an internal counter.
     * @return A key which is unique in this list instance.
     */
    protected synchronized int generateKey() {
        return counter++;
    }

}
