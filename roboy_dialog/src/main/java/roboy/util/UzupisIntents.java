package roboy.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//TODO unify & merge with memoryRelationships
public enum UzupisIntents {
    INTRO("INTRO"),
    NAME("NAME"),
    FRUIT("FRUIT"),
    COLOR("COLOR"),
    ANIMAL("ANIMAL"),
    WORD("WORD"),
    APPLES("APPLES"),
    PLANT("PLANT");

    public String type;

    UzupisIntents(String type) {
        this.type=type;
    }

    private static final List<UzupisIntents> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static UzupisIntents randomIntent()  {
        UzupisIntents ret;
        do {
            ret = VALUES.get(RANDOM.nextInt(SIZE));
        } while (ret==INTRO && ret==NAME);
        return ret;
    }
}