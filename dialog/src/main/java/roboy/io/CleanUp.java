package roboy.io;

/**
 * Devices that need extra cleaning operation on destruction implement this.
 */
public interface CleanUp {
    public void cleanup();
}
