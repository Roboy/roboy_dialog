package roboy.ros;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import org.ros.node.topic.Subscriber;
import roboy.context.Context;
import roboy.emotions.RoboyEmotion;
import roboy.util.ConfigManager;
import roboy_communication_cognition.*;
import roboy_communication_control.*;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class RosMainNode extends AbstractNodeMain {

//    private static RosMainNode node;
    private CountDownLatch rosConnectionLatch;
    private RosManager services = new RosManager();
    protected Object resp;

    String warning = "Trying to talk to ROS package %s, but it's not initialized or deactivated";
    String memoryFailure = "{" +
            "status : \"FAIL\", " +
            "message : \"Memory client not initialized.\"" +
            "}";

    final Logger LOGGER = LogManager.getLogger();

    public RosMainNode() {

        if (!ConfigManager.ROS_ENABLED) {
            LOGGER.warn("ROS is disabled in config.properties, but you are still trying to use it");
        } else {
            services = new RosManager();

            if (ConfigManager.ROS_MASTER_IP == null || ConfigManager.ROS_MASTER_IP.isEmpty()) {
                LOGGER.error("Could not connect to ROS master. ROS will be unavailable. Check if ROS_MASTER_IP is specified " +
                        "correctly or disable ROS in config.properties file.");
            }

            URI masterURI = URI.create("http://" + ConfigManager.ROS_MASTER_IP + ":11311");

            NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ConfigManager.ROS_MASTER_IP);
            nodeConfiguration.setMasterUri(masterURI);

            // Create and start ROS Node
            nodeConfiguration.setNodeName("roboy_dialog");
            NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
            nodeMainExecutor.execute(this, nodeConfiguration);
            rosConnectionLatch = new CountDownLatch(1);
            waitForLatchUnlock(rosConnectionLatch, "ROS init");
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roboy_dialog");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        services.initialize(connectedNode);
        rosConnectionLatch.countDown();
    }

    public void PerformMovement(String bodyPart, String name) throws InterruptedException {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", ConfigManager.ACTION_CLIENT_SCRIPT, bodyPart, name);
            pb.redirectError();
            pb.start();
            pb.wait();
        }catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    public boolean SynthesizeSpeech(String text) {

        if(services.notInitialized(RosServiceClients.SPEECHSYNTHESIS)) {
            // FALLBACK RETURN VALUE
            return false;
        }

        ServiceClient<TalkRequest, TalkResponse> speechSynthesisClient = services.getService(RosServiceClients.SPEECHSYNTHESIS);
        rosConnectionLatch = new CountDownLatch(1);
        TalkRequest request = speechSynthesisClient.newMessage();
        request.setText(text);
        ServiceResponseListener<TalkResponse> listener = new ServiceResponseListener<TalkResponse>() {
            @Override
            public void onSuccess(TalkResponse response) {
//                System.out.println(response.getSuccess());
                resp = response.getSuccess();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        speechSynthesisClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, speechSynthesisClient.getName().toString());
        return ((boolean) resp);
    }

    public String RecognizeSpeech() {

        if(services.notInitialized(RosServiceClients.STT)) {
            // FALLBACK RETURN VALUE
            return null;
        }

        ServiceClient<RecognizeSpeechRequest, RecognizeSpeechResponse> sttClient = services.getService(RosServiceClients.STT);
        rosConnectionLatch = new CountDownLatch(1);
        RecognizeSpeechRequest request = sttClient.newMessage();
        ServiceResponseListener<RecognizeSpeechResponse> listener = new ServiceResponseListener<RecognizeSpeechResponse>() {
            @Override
            public void onSuccess(RecognizeSpeechResponse response) {
//                System.out.println(response.getText());
                resp = response.getText();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        sttClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, sttClient.getName().toString());
        return ((String) resp);

    }

    public String GenerateAnswer(String question) {

        if (question == null || question.isEmpty()) {
            LOGGER.warn("GenerateAnswer ROS service received NULL on input");
            return null;
        }


        if(services.notInitialized(RosServiceClients.GENERATIVE)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<GenerateAnswerRequest, GenerateAnswerResponse> generativeClient = services.getService(RosServiceClients.GENERATIVE);
        rosConnectionLatch = new CountDownLatch(1);
        GenerateAnswerRequest request = generativeClient.newMessage();
        request.setTextInput(question);
        ServiceResponseListener<GenerateAnswerResponse> listener = new ServiceResponseListener<GenerateAnswerResponse>() {
            @Override
            public void onSuccess(GenerateAnswerResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getTextOutput();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        generativeClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, generativeClient.getName().toString());
        String answer = (String) resp;
        answer.replace("n't", "not");
        return answer;
    }

    public boolean ShowEmotion(RoboyEmotion emotion) {

        return ShowEmotion(emotion.type);
    }

    public boolean ShowEmotion(String emotion) {

        if(services.notInitialized(RosServiceClients.EMOTION)) {
            // FALLBACK RETURN VALUE
            LOGGER.info("RoboyEmotion not initialized");
            return false;
        }
        ServiceClient<ShowEmotionRequest, ShowEmotionResponse> emotionClient = services.getService(RosServiceClients.EMOTION);
        rosConnectionLatch = new CountDownLatch(1);
        ShowEmotionRequest request = emotionClient.newMessage();
        request.setEmotion(emotion);
        ServiceResponseListener<ShowEmotionResponse> listener = new ServiceResponseListener<ShowEmotionResponse>() {
            @Override
            public void onSuccess(ShowEmotionResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getSuccess();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        emotionClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, emotionClient.getName().toString());
        return ((boolean) resp);
    }

    @Deprecated
    public String CreateMemoryQuery(String query) {

        if(services.notInitialized(RosServiceClients.CREATEMEMORY)) {
            // FALLBACK RETURN VALUE
            return memoryFailure;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> createMemoryClient = services.getService(RosServiceClients.CREATEMEMORY);
        rosConnectionLatch = new CountDownLatch(1);
        DataQueryRequest createRequest = createMemoryClient.newMessage();
        // TODO set the header
        createRequest.setPayload(query);
        ServiceResponseListener<DataQueryResponse> listener = new ServiceResponseListener<DataQueryResponse>() {
            @Override
            public void onSuccess(DataQueryResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getAnswer();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        createMemoryClient.call(createRequest, listener);
        waitForLatchUnlock(rosConnectionLatch, createMemoryClient.getName().toString());
        return ((String) resp);
    }
    @Deprecated
    public String UpdateMemoryQuery(String query) {

        if(services.notInitialized(RosServiceClients.UPDATEMEMORY)) {
            // FALLBACK RETURN VALUE
            return memoryFailure;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> updateMemoryClient = services.getService(RosServiceClients.UPDATEMEMORY);
        rosConnectionLatch = new CountDownLatch(1);
        DataQueryRequest updateRequest = updateMemoryClient.newMessage();
        // TODO set the header
        updateRequest.setPayload(query);
        ServiceResponseListener<DataQueryResponse> listener = new ServiceResponseListener<DataQueryResponse>() {
            @Override
            public void onSuccess(DataQueryResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getAnswer();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        updateMemoryClient.call(updateRequest, listener);
        waitForLatchUnlock(rosConnectionLatch, updateMemoryClient.getName().toString());
        return ((String) resp);
    }

    @Deprecated
    public String GetMemoryQuery(String query) {

        if(services.notInitialized(RosServiceClients.GETMEMORY)) {
            // FALLBACK RETURN VALUE
            return memoryFailure;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> getMemoryClient = services.getService(RosServiceClients.GETMEMORY);
        rosConnectionLatch = new CountDownLatch(1);
        DataQueryRequest getRequest = getMemoryClient.newMessage();
        // TODO set the header
        getRequest.setPayload(query);
        ServiceResponseListener<DataQueryResponse> listener = new ServiceResponseListener<DataQueryResponse>() {
            @Override
            public void onSuccess(DataQueryResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getAnswer();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        getMemoryClient.call(getRequest, listener);
        waitForLatchUnlock(rosConnectionLatch, getMemoryClient.getName().toString());
        return ((String) resp);
    }

    @Deprecated
    public String DeleteMemoryQuery(String query) {

        if(services.notInitialized(RosServiceClients.DELETEMEMORY)) {
            // FALLBACK RETURN VALUE
            return memoryFailure;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> deleteMemoryClient = services.getService(RosServiceClients.DELETEMEMORY);
        rosConnectionLatch = new CountDownLatch(1);
        DataQueryRequest getRequest = deleteMemoryClient.newMessage();
        // TODO set the header
        getRequest.setPayload(query);
        ServiceResponseListener<DataQueryResponse> listener = new ServiceResponseListener<DataQueryResponse>() {
            @Override
            public void onSuccess(DataQueryResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getAnswer();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        deleteMemoryClient.call(getRequest, listener);
        waitForLatchUnlock(rosConnectionLatch, deleteMemoryClient.getName().toString());
        return ((String) resp);
    }
    @Deprecated
    public String CypherMemoryQuery(String query) {

        if(services.notInitialized(RosServiceClients.CYPHERMEMORY)) {
            // FALLBACK RETURN VALUE
            return memoryFailure;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> cypherMemoryClient = services.getService(RosServiceClients.CYPHERMEMORY);
        rosConnectionLatch = new CountDownLatch(1);
        DataQueryRequest cypherRequest = cypherMemoryClient.newMessage();
        // TODO set the header
        cypherRequest.setPayload(query);
        ServiceResponseListener<DataQueryResponse> listener = new ServiceResponseListener<DataQueryResponse>() {
            @Override
            public void onSuccess(DataQueryResponse response) {
//                System.out.println(response.getTextOutput());
                resp = response.getAnswer();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        cypherMemoryClient.call(cypherRequest, listener);
        waitForLatchUnlock(rosConnectionLatch, cypherMemoryClient.getName().toString());
        return ((String) resp);
    }

    public Object DetectIntent(String sentence) {

        if(services.notInitialized(RosServiceClients.INTENT)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DetectIntentRequest, DetectIntentResponse> intentClient = services.getService(RosServiceClients.INTENT);
        rosConnectionLatch = new CountDownLatch(1);
        DetectIntentRequest request = intentClient.newMessage();
        request.setSentence(sentence);
        ServiceResponseListener<DetectIntentResponse> listener = new ServiceResponseListener<DetectIntentResponse>() {
            @Override
            public void onSuccess(DetectIntentResponse response) {
                Object[] intent = {response.getIntent(), response.getDistance()};
                resp = intent;
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        intentClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, intentClient.getName().toString());
        return resp;
    }

    public void addListener(MessageListener listener, RosSubscribers subscriber) {
        if(services.notInitialized(subscriber)) {

            LOGGER.warn(String.format(warning, subscriber.rosPackage));
            return;
        }
        Subscriber s = services.getSubscriber(subscriber);
        s.addMessageListener(listener);
    }

    public boolean ApplyFilter(String filterName) {

        if(services.notInitialized(RosServiceClients.SNAPCHATFILTER)) {
            // FALLBACK RETURN VALUE
            LOGGER.info("ApplyFilter not initialized");
            return false;
        }

        ServiceClient<ApplyFilterRequest, ApplyFilterResponse> snapchatFilterClient = services.getService(RosServiceClients.SNAPCHATFILTER);
        rosConnectionLatch = new CountDownLatch(1);
        ApplyFilterRequest request = snapchatFilterClient.newMessage();
        request.setName(filterName);

        ServiceResponseListener<ApplyFilterResponse> listener = new ServiceResponseListener<ApplyFilterResponse>() {
            @Override
            public void onSuccess(ApplyFilterResponse response) {
//                System.out.println(response.getSuccess());
                resp = response.getSuccess();
                rosConnectionLatch.countDown();
            }

            @Override
            public void onFailure(RemoteException e) {
                rosConnectionLatch.countDown();
                throw new RosRuntimeException(e);
            }
        };
        snapchatFilterClient.call(request,  listener);
        waitForLatchUnlock(rosConnectionLatch, snapchatFilterClient.getName().toString());
        return ((boolean) resp);
    }

    /**
     * Helper method to block the calling thread until the latch is zeroed by some other task.
     * @param latch Latch to wait for.
     * @param latchName Name to be used in log messages for the given latch.
     */
    private void waitForLatchUnlock(CountDownLatch latch, String latchName) {
        try {

            latch.await();

        } catch (InterruptedException ie) {
            LOGGER.warn("Continuing before " + latchName + " latch was released");
        }
    }

}

