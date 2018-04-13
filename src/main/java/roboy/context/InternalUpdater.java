package roboy.context;

/**
 * An updater which can be called from inside DM to update a Value or ValueHistory.
 *
 * @param <T> The target Value or ValueHistory.
 * @param <V> The data type stored in the target.
 */
public class InternalUpdater<T extends AbstractValue<V>, V> {
    AbstractValue<V> target;

    public InternalUpdater(T target) {
        this.target = target;
    }

    public synchronized void updateValue(V value) {
        target.updateValue(value);
    }
}
