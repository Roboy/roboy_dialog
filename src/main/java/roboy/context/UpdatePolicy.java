package roboy.context;

/**
 * For a ContextObjects which should be updated on-the-fly (or at regular intervals),
 * this class should take care of fetching and passing the values.
 */
public interface UpdatePolicy {
    void update();
}
