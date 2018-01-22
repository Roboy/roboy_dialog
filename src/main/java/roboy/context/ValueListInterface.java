package roboy.context;

import java.util.Map;

/**
 * Keeps a list of all values that have been stored in it.
 */
public interface ValueListInterface<K,V> {

    V getLastValue();

    V getValue(K key);

    Map<K, V> getLastNValues(int n);

    K storeValue(V key);
}
