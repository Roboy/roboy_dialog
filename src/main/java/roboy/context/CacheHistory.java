package roboy.context;

import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * An implementation of the CacheHistory which stores strings in a file as backup.
 * Lessons learned: Instant may offer cool timestamps, but they are not unique. Need UUID or similar.
 */
public class CacheHistory extends History<String,String> {
    LoadingCache<String, String> data;
    PrintWriter writer;
    BufferedReader reader;

    CacheHistory(String fileName, int secondsUntilExpire, int maxCacheSize) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
        reader = new BufferedReader(new FileReader(fileName));
        data = CacheBuilder.newBuilder()
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

    @Override
    public String storeValue(String value) {
        String key = UUID.randomUUID().toString();
        saveValue(key, value);
        return key;
    }

    @Override
    public String getValue(String key) {
        try {
            return data.get(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveValue(String key, String value) {
        data.put(key, value);
        writer.println(key.toString() + "\t" + value);
        writer.flush();
    }
}
