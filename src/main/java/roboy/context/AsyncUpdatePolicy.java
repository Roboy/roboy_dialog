package roboy.context;

/**
 * For Attributes and Objects which should be updated on-the-fly (or at regular intervals),
 * this class should take care of fetching and passing the values.
 */
public abstract class AsyncUpdatePolicy {
    protected abstract void update();
}
