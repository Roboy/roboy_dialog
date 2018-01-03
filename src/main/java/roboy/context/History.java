package roboy.context;

import java.time.Instant;

/**
 * This enables saving basic History information by the key type T and data/value type V.
 */
public interface History<K,V> {

    V getValue(K key);

    void saveValue(K key, V value);

    K storeValue(V key);

    K generateKey();
}
