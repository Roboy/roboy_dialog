package roboy.context;

import roboy.context.dataTypes.DataType;

/**
 * An updater which can be called by DM to store values within an Attribute.
 * @param <V>
 */
public interface DirectUpdatePolicy<V extends DataType> {
    public void putValue(V value);
}
