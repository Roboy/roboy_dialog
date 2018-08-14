package roboy.util;

/**
 * A simple tuple class to store two values. Like javafx.util.pair just more simple, but accessible from maven.
 * @param <KEY> First value of the tuple
 * @param <VALUE> Second value of the tuple
 */
public class Pair<KEY, VALUE> {
    private KEY key = null;
    private VALUE value = null;

    public Pair(KEY key, VALUE value){
        this.key = key;
        this.value = value;
    }

    public KEY getKey(){
        return this.key;
    }

    public VALUE getValue(){
        return this.value;
    }
}
