package roboy.util;

import java.util.List;
import java.util.Map;

public class JsonQAValues {
    private Map<String, List<String>> questions;
    private Map<String, List<String>> successAnswers;
    private Map<String, List<String>> failureAnswers;
    private Map<String, List<String>> followUp;
    private Map<String, List<String>> answersFollowUp;

    public JsonQAValues(Map<String, List<String>> questions, Map<String, List<String>> successAnswers,
                        Map<String, List<String>> failureAnswers, Map<String, List<String>> followUp,
                        Map<String, List<String>> answersFollowUp) {
        this.questions = questions;
        this.successAnswers = successAnswers;
        this.failureAnswers = failureAnswers;
        this.followUp = followUp;
        this.answersFollowUp = answersFollowUp;
    }

    public Map<String, List<String>> getQuestions() {
        return questions;
    }
    public Map<String, List<String>> getSuccessAnswers() {
        return successAnswers;
    }
    public Map<String, List<String>> getFailureAnswers() {
        return failureAnswers;
    }
    public Map<String, List<String>> getFollowUpQuestions() {
        return followUp;
    }
    public Map<String, List<String>> getFollowUpAnswers() {
        return answersFollowUp;
    }
}
