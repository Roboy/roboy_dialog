package roboy.context;

/**
 * An updater which can be called from inside DM to update a Value.
 * @param <V> The value type.
 */
public class InternalValueUpdater<V> {
    private Value<V> target;

    protected InternalValueUpdater(Value<V> target) {
        this.target = target;
    }
    public synchronized void putValue(V value) {
        target.updateValue(value);
    }
}
