package roboy.util;

import java.util.Map;

public class JsonEntryModel {
    RandomList<String> Q;
    Map<String, RandomList<String>> A;
    Map<String, RandomList<String>> FUP;

    public RandomList<String> getQuestions() {
        return Q;
    }

    public Map<String, RandomList<String>> getAnswers() {
        return A;
    }

    public Map<String, RandomList<String>> getFUP() {
        return FUP;
    }
}
