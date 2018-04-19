package roboy.dialog.states.searchStates;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.dialog.states.definitions.State;
import roboy.dialog.states.definitions.StateParameters;
import roboy.linguistics.Linguistics;
import roboy.linguistics.sentenceanalysis.Interpretation;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import roboy.talk.PhraseCollection;
import roboy.util.RandomList;

import java.io.IOException;

class KnowledgeGraphClient {
    private static String API_KEY = "000000000000000000000000000000000000000";
    final static String KGS_URL = "https://kgsearch.googleapis.com/v1/entities:search";
    final static int DEFAULT_LIMIT = 1;
    final static boolean DEFAULT_INDENT = true;

    public KnowledgeGraphClient() {}
    public KnowledgeGraphClient(String apiKey) {
        API_KEY = apiKey;
    }

    public String getKnowledgeGraphSearchResponse(String query) throws IOException {
        return getKnowledgeGraphSearchResponse(query, DEFAULT_LIMIT, DEFAULT_INDENT, API_KEY);
    }

    public String getKnowledgeGraphSearchResponse(String query, int limit) throws IOException {
        return getKnowledgeGraphSearchResponse(query, limit, DEFAULT_INDENT, API_KEY);
    }

    public String getKnowledgeGraphSearchResponse(String query, int limit, boolean indent) throws IOException {
        return getKnowledgeGraphSearchResponse(query, limit, indent, API_KEY);
    }

    public String getKnowledgeGraphSearchResponse(String query, int limit, boolean indent, String apiKey) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        GenericUrl url = new GenericUrl(KGS_URL);
        url.put("query", query);
        url.put("limit", limit);
        url.put("indent", indent);
        url.put("key", apiKey);
        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        return httpResponse.parseAsString();
    }
}

public class GoogleQuestionAnsweringState extends State {
    private final static String TRANSITION_FINISHED_ANSWERING = "finishedQuestionAnswering";
    private final static int MAX_NUM_OF_QUESTIONS = 5;
    private final static RandomList<String> reenteringPhrases = PhraseCollection.QUESTION_ANSWERING_REENTERING;

    private final Logger LOGGER = LogManager.getLogger();

    private int questionsAnswered = 0;

    public GoogleQuestionAnsweringState(String stateIdentifier, StateParameters params) {
        super(stateIdentifier, params);
    }

    @Override
    public Output act() {
        if (questionsAnswered > 0) {
            return Output.say(reenteringPhrases.getRandomElement());
        }
        return Output.say("I'm pretty good at answering questions about myself and other stuff. What would you like to know?");
    }

    @Override
    public Output react(Interpretation input) {
        KnowledgeGraphClient kgClient = new KnowledgeGraphClient();
        String answer = "I have no words!";
        String result = "";
        try {
            result = kgClient.getKnowledgeGraphSearchResponse(input.getFeature(Linguistics.SENTENCE).toString());
            questionsAnswered++;
        } catch (IOException e) {
            LOGGER.error(" -> IO exception while getting Knowledge Graph response: " + e.getMessage());
        }

        return Output.say(!result.equals("") ? result : answer);
    }

    @Override
    public State getNextState() {
        if (questionsAnswered > MAX_NUM_OF_QUESTIONS) { // enough questions answered --> finish asking
            return getTransition(TRANSITION_FINISHED_ANSWERING);
        } else {
            return this;
        }
    }
}
