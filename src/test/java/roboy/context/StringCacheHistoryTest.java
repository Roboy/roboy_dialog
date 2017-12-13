package roboy.context;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Testing cache storage functionality.
 */
public class StringCacheHistoryTest {
    @Test
    public void testCacheForFun() throws IOException, ExecutionException {
        StringCacheHistory history = new StringCacheHistory("cacheTestStorage.txt",10,25);
        String[] timeStamps = new String[20];
        for(int i = 0; i < 20; i++) {
            timeStamps[i] = history.saveValue("value " + i);
        }
        for(int i = 0; i < 20; i++) {
            assertEquals("value " + i, history.getValue(timeStamps[i]));
        }
    }
}