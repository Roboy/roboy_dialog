package roboy.context;

import java.util.Observable;

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
        clearChanged();
    }
}
