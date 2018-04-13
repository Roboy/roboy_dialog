package roboy.context;

/**
 * Stores a single value.
 * On update, the value is overwritten.
 */
public interface AbstractValue<V> {
    V getValue();

    void updateValue(V value);
}
