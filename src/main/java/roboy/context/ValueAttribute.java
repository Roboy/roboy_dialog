package roboy.context;

public interface ValueAttribute<V> {

    V getValue();

    void updateValue(V key);
}
