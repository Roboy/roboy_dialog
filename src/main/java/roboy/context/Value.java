package roboy.context;

/**
 * Stores a single value of type V.
 */
public class Value<V> implements AbstractValue<V> {
    private volatile V value = null;

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void updateValue(V value) {
        this.value = value;
    }

}
