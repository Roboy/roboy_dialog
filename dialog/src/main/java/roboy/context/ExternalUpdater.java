package roboy.context;

/**
 * For Values which should be updated upon incoming data or at regular intervals,
 * this class fetches and passes the values.
 */
public abstract class ExternalUpdater {
    protected abstract void update();
}
