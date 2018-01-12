package roboy.context;

import roboy.context.dataTypes.DataType;

public class SimpleValueAttribute<V extends DataType> implements ValueAttribute<V> {
    V value = null;

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void updateValue(V value) {
        this.value = value;
    }

}
