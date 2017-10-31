package roboy.ros;

import org.ros.namespace.GraphName;
import org.ros.node.*;

public class RosMainNodeOffline extends RosMainNode {
    String memoryResponse = "{" +
            "status : \"FAIL\", " +
            "message : \"Neo4j memory cannot be queried in offline mode.\"" +
            "}";

    public RosMainNodeOffline() {
        System.out.println("Starting in offline mode without ROS connection.");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roboy_dialog");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        System.out.println("No ROS clients initialized in offline mode.");
    }

    public boolean SynthesizeSpeech(String text) {
        return false;
    }

    public String RecognizeSpeech() {
        return "";
    }

    public String GenerateAnswer(String question) {
        return "";
    }

    public boolean ShowEmotion(String emotion) {
        return false;
    }

    public String CreateMemoryQuery(String query) {
        return memoryResponse;
    }

    public String UpdateMemoryQuery(String query) {
        return memoryResponse;
    }

    public String GetMemoryQuery(String query) {
        return memoryResponse;
    }

    public String DeleteMemoryQuery(String query) {
        return memoryResponse;
    }

    public String CypherMemoryQuery(String query) {
        return memoryResponse;
    }

    public Object DetectIntent(String sentence) {
        return "";
    }
}
