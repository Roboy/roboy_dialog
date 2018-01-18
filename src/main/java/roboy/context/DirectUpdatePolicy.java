package roboy.context;

import roboy.context.dataTypes.DataType;

public interface DirectUpdatePolicy<V extends DataType> {
    public void putValue(V value);
}
