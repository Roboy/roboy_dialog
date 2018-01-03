package roboy.context;

import java.util.HashMap;
/**
 * Simplistic (and probably not very efficient) HashMap history.
 * generateKey is type-dependent, must be implemented by subclasses.
 */
public abstract class SimpleHistory<K, V> implements History<K,V> {
    protected HashMap<K,V> data;

    protected SimpleHistory() {
        data = new HashMap<K,V>();
    }

    @Override
    public V getValue(K key) {
        return data.containsKey(key) ? data.get(key) : null;
    }

    @Override
    public void saveValue(K key, V value) {
        data.put(key, value);
    }

    @Override
    public K storeValue(V value) {
        K key = generateKey();
        data.put(key, value);
        return key;
    }

    @Override
    public abstract K generateKey();
}
