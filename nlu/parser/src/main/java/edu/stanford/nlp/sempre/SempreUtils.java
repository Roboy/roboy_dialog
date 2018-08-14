package edu.stanford.nlp.sempre;

import edu.stanford.nlp.sempre.roboy.utils.logging.LogInfoToggle;
import fig.basic.MapUtils;

import java.util.Map;

/**
 * Created by joberant on 10/18/14.
 */
public final class SempreUtils {
  private SempreUtils() { }

  // "java.util.ArrayList" => "java.util.ArrayList"
  // "TypeLookup" => "edu.stanford.nlp.sempre.TypeLookup"
  public static String resolveClassName(String name) {
    if (name.startsWith("edu.") || name.startsWith("org.") ||
        name.startsWith("com.") || name.startsWith("net."))
      return name;
    return "edu.stanford.nlp.sempre." + name;
  }

  public static <K, V> void logMap(Map<K, V> map, String desc) {
    LogInfoToggle.begin_track("Logging %s map", desc);
    for (K key : map.keySet())
      LogInfoToggle.log(key + "\t" + map.get(key));
    LogInfoToggle.end_track();
  }

  public static void addToDoubleMap(Map<String, Double> mutatedMap, Map<String, Double> addedMap) {
    for (String key : addedMap.keySet())
      MapUtils.incr(mutatedMap, key, addedMap.get(key));
  }
}
