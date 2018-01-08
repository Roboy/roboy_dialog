package roboy.context;

import java.util.Map;

/**
 * This enables saving basic History information by the key type T and data/value type V.
 */
public interface History<K,V> {

    V getLastValue();

    V getValue(K key);

    Map<K, V> getLastNValues(int n);

    K storeValue(V key);
}
