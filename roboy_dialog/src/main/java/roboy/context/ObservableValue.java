package roboy.context;

import java.util.Observable;

/**
 * A Value that supports adding Observers. These will be notified whenever a new value is added.
 * @param <V>
 */
public class ObservableValue<V> extends Observable implements AbstractValue<V> {
    private V value = null;

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public synchronized void updateValue(V value) {
        this.value = value;
        setChanged();
        notifyObservers();
    }
}
