package roboy.context;

/**
 * An updater which can be called from inside DM to add values into a list.
 * @param <V> The value type.
 */
public class InternalListUpdater<K extends Integer, V> {
    private ValueList<K,V> target;

    protected InternalListUpdater(ValueList<K,V> target) {
        this.target = target;
    }

    public synchronized K putValue(V value) {
        return target.storeValue(value);
    }
}
