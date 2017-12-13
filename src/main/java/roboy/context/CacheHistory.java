package roboy.context;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.*;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * One of the approaches to test history saving.
 * Caches could be useful for large data, if usually only the recent ones are needed.
 */
public abstract class CacheHistory<T> extends History {
    LoadingCache<Instant,T> history;

    <T> CacheHistory(int secondsUntilExpire, int maxCacheSize) throws IOException {
        history = CacheBuilder.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(secondsUntilExpire, TimeUnit.SECONDS)
                .build(
                        new CacheLoader() {
                            @Override
                            public T load(Object key) throws Exception {
                                return (T) getValue((Instant) key);
                            }
                        });

    }

    abstract T getValue(Instant key);

    abstract void saveValue(Instant key, T value);
}
