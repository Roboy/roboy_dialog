package roboy.context;

import java.time.Instant;

/**
 * This enables saving basic History information by the data type T.
 */
public abstract class History<T> {
    // TODO what is the best way to save time-dependent information, to query by interval?
    // Even if concurrency becomes a thing, I would first try to enforce synchronous access through methods.

    private Instant getCurrentTimeStamp() {
        return Instant.now();
    }
}
