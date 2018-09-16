package roboy.util;

import java.util.Map;
import java.util.Objects;

public class JsonEntryModel {
    String intent;
    RandomList<String> Q;
    Map<String, RandomList<String>> A;
    Map<String, RandomList<String>> FUP;

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public void setQuestions(RandomList<String> q) {
        Q = q;
    }

    public void setAnswers(Map<String, RandomList<String>> a) {
        A = a;
    }

    public void setFUP(Map<String, RandomList<String>> FUP) {
        this.FUP = FUP;
    }

    public String getIntent() {
        return intent;
    }

    public RandomList<String> getQuestions() {
        return Q;
    }

    public Map<String, RandomList<String>> getAnswers() {
        return A;
    }

    public Map<String, RandomList<String>> getFUP() {
        return FUP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonEntryModel)) return false;
        JsonEntryModel that = (JsonEntryModel) o;
        return Objects.equals(getIntent(), that.getIntent()) &&
                Objects.equals(Q, that.Q) &&
                Objects.equals(A, that.A) &&
                Objects.equals(getFUP(), that.getFUP());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getIntent(), Q, A, getFUP());
    }

    @Override
    public String toString() {
        return "JsonEntryModel{" +
                "intent='" + intent + '\'' +
                ", Q=" + Q +
                ", A=" + A +
                ", FUP=" + FUP +
                '}';
    }
}
