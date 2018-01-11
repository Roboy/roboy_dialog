package roboy.context;

public interface ValueAttribute<K,V> {

    V getValue();

    K storeValue(V key);
}
