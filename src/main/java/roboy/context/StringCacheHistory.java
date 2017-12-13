package roboy.context;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * An implementation of the CacheHistory which stores strings in a file as backup.
 *
 * Lessons learned: Instant may offer cool timestamps, but they are not unique.
 */
public class StringCacheHistory extends History {
    LoadingCache<String, String> history;
    PrintWriter writer;
    BufferedReader reader;

    StringCacheHistory(String fileName, int secondsUntilExpire, int maxCacheSize) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
        reader = new BufferedReader(new FileReader(fileName));
        history = CacheBuilder.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(secondsUntilExpire, TimeUnit.SECONDS)
                .build(
                        new CacheLoader() {
                            @Override
                            public String load(Object key) throws Exception {
                                String fetch = getValueFromFile((String) key);
                                String[] fetchArr  = fetch.split("\t");
                                System.out.println("File storage lookup: " + fetch);
                                return fetchArr[1];
                            }
                        });

    }

    private String getValueFromFile(String key) {
        Stream stream = reader.lines().filter(Predicates.containsPattern(key.toString()));
        Optional<String> match = stream.findFirst();
        return match.orElse("|");
    }

    public String getValue(String key) throws ExecutionException {
        return history.get(key);
    }

    String saveValue(String value) {
        String key = UUID.randomUUID().toString();
        history.put(key, value);
        writer.println(key.toString() + "\t" + value);
        writer.flush();
        return key;
    }
}
