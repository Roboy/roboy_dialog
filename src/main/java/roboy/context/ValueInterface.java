package roboy.context;

import java.util.ArrayList;

/**
 * This is the interface over which Context values can be queried.
 * Initialize as static field of the Context class.
 * Add your Value implementation class and its return type as generic parameters.
 *
 * @param <I> An implementation of AbstractValue, such as the standard Value, ROS or Observable.
 * @param <V> The type of data stored within the Value instance.
 */
public class ValueInterface<I extends AbstractValue<V>, V> {

    protected I value;

    protected ValueInterface(I value) {
        this.value = value;
    }

    I getContextObject() {
        return value;
    }

    /**
     * Get the last element saved into the corresponding Value instance.
     */
    public V getValue() {
        return value.getValue();
    }
}
