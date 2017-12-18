package roboy.context;

/**
 * Represents one object attribut (with a value history) for a ContextObject.
 */
public abstract class Attribute<K,V> {
    private History<K,V> history;
    private K lastKey;

    public abstract V getLatestValue();

    public abstract V getValue(K key);
}
