package roboy.context;

/**
 * Stores an attribute with a single value.
 * Used for attributes where the history (previous values) are not of interest.
 */
public interface ValueAttribute<V> {

    V getValue();

    void updateValue(V key);
}
