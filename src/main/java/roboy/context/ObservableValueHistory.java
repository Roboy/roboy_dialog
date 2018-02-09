package roboy.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ObservableValueHistory<V> extends Observable implements AbstractValueHistory<Integer, V>  {

    private volatile int counter;
    private HashMap<Integer, V> data;

    private synchronized V getValue(int key) {
        return data.getOrDefault(key, null);
    }

    public ObservableValueHistory() {
        data = new HashMap<>();
        counter = 0;
    }

    /**
     * Standard observable method, the added observers will be notified of value changes.
     */
    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public Map<Integer, V> getLastNValues(int n) {
        HashMap<Integer, V> response = new HashMap<>();
        int lastToRetrieve = counter - Math.min(n, counter);
        for (int i = counter - 1; i >= lastToRetrieve; i--) {
            response.put(i - lastToRetrieve, getValue(i));
        }
        return response;
    }

    @Override
    public V getValue() {
        if (counter == 0) {
            return null;
        } else {
            return getValue(counter - 1);
        }
    }

    @Override
    public synchronized void updateValue(V value) {
        Integer key = generateKey();
        data.put(key, value);
        // Efficiency should be tested.
        setChanged();
        notifyObservers();
        clearChanged();
    }

    private synchronized int generateKey() {
        return counter++;
    }

    @Override
    public int valuesAddedSinceStart() {
        return counter;
    }
}
