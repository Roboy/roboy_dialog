package roboy.context;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Testing cache storage functionality.
 * This was an experimental implementation, so the test was not added to standard DM test set.
 */
public class CacheHistoryTest {
    public void testCacheForFun() throws IOException, ExecutionException {
        int VALUES = 20;
        int TIMEOUT = 1; //seconds
        int CACHESIZE = 21;
        CacheHistory history = new CacheHistory("cacheTestStorage.txt", TIMEOUT, CACHESIZE);
        String[] UUIDs = new String[VALUES];
        for(int i = 0; i < VALUES; i++) {
            UUIDs[i] = history.storeValue("value " + i);
        }
        for(int i = 0; i < VALUES; i++) {
            assertEquals("value " + i, history.getValue(UUIDs[i]));
        }
    }
}