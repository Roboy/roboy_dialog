package roboy.util;

import java.util.List;
import java.util.Map;

public class JsonEntryModel {
    List<String> Q;
    Map<String, List<String>> A;
    Map<String, List<String>> FUP;

    public List<String> getQuestions() {
        return Q;
    }

    public Map<String, List<String>> getAnswers() {
        return A;
    }

    public Map<String, List<String>> getFUP() {
        return FUP;
    }
}
