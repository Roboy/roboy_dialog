package roboy.context;

/**
 * Represents one object attribute (with a value history) for a ContextObject.
 */
public abstract class Attribute<K,V> {
    protected History<K,V> history;
    protected K lastKey;

    public abstract V getLatestValue();

    public abstract V getValue(K key);
}
