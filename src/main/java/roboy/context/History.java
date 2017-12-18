package roboy.context;

import java.time.Instant;

/**
 * This enables saving basic History information by the key type T and data/value type V.
 */
public abstract class History<K,V> {

    public abstract V getValue(K key);

    public abstract void saveValue(K key, V value);

    public abstract V storeValue(K key);
}
