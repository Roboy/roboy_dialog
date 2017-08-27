package roboy.ros;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import roboy_communication_cognition.*;
import roboy_communication_control.*;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class RosMainNode extends AbstractNodeMain {

//    private static RosMainNode node;
    private CountDownLatch rosConnectionLatch;
    private RosManager clients = new RosManager();
    protected Object resp;
    public boolean STARTUP_SUCCESS = true;

    public RosMainNode() {
        clients = new RosManager();

        String hostName = System.getenv("ROS_HOSTNAME");
        //String hostName = "10.183.122.142";
        if (hostName == null || hostName.isEmpty()) {
            System.out.println("Could not find ROS hostname. ROS will be unavailable. Set ROS_HOSTNAME environmental variable.");
        }

        URI masterURI = URI.create("http://" + hostName + ":11311");

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(hostName);
        nodeConfiguration.setMasterUri(masterURI);

        // Create and start ROS Node
        nodeConfiguration.setNodeName("roboy_dialog");
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(this, nodeConfiguration);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roboy_dialog");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            clients.initialize(connectedNode);
        } catch (RuntimeException e) {
            System.out.println("ROS client manager failed to initialize!");
            STARTUP_SUCCESS = false;
        }
    }

    public boolean SynthesizeSpeech(String text) {

        if(clients.notInitialized(RosClients.SPEECHSYNTHESIS)) {
            // FALLBACK RETURN VALUE
            return false;
        }

        ServiceClient<TalkRequest, TalkResponse> speechSynthesisClient = clients.getServiceClient(RosClients.SPEECHSYNTHESIS);
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

        if(clients.notInitialized(RosClients.STT)) {
            // FALLBACK RETURN VALUE
            return null;
        }

        ServiceClient<RecognizeSpeechRequest, RecognizeSpeechResponse> sttClient = clients.getServiceClient(RosClients.STT);
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

        if(clients.notInitialized(RosClients.GENERATIVE)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<GenerateAnswerRequest, GenerateAnswerResponse> generativeClient = clients.getServiceClient(RosClients.GENERATIVE);
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
        return ((String) resp);
    }

    public boolean ShowEmotion(String emotion) {

        if(clients.notInitialized(RosClients.EMOTION)) {
            // FALLBACK RETURN VALUE
            return false;
        }
        ServiceClient<ShowEmotionRequest, ShowEmotionResponse> emotionClient = clients.getServiceClient(RosClients.EMOTION);
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

    public String CreateMemoryQuery(String query) {

        if(clients.notInitialized(RosClients.CREATEMEMORY)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> createMemoryClient = clients.getServiceClient(RosClients.CREATEMEMORY);
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

    public String UpdateMemoryQuery(String query) {

        if(clients.notInitialized(RosClients.UPDATEMEMORY)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> updateMemoryClient = clients.getServiceClient(RosClients.UPDATEMEMORY);
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

    public String GetMemoryQuery(String query) {

        if(clients.notInitialized(RosClients.GETMEMORY)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> getMemoryClient = clients.getServiceClient(RosClients.GETMEMORY);
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

    public String DeleteMemoryQuery(String query) {

        if(clients.notInitialized(RosClients.DELETEMEMORY)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> deleteMemoryClient = clients.getServiceClient(RosClients.DELETEMEMORY);
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

    public String CypherMemoryQuery(String query) {

        if(clients.notInitialized(RosClients.CYPHERMEMORY)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DataQueryRequest, DataQueryResponse> cypherMemoryClient = clients.getServiceClient(RosClients.CYPHERMEMORY);
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

        if(clients.notInitialized(RosClients.INTENT)) {
            // FALLBACK RETURN VALUE
            return null;
        }
        ServiceClient<DetectIntentRequest, DetectIntentResponse> intentClient = clients.getServiceClient(RosClients.INTENT);
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

    /**
     * Helper method to block the calling thread until the latch is zeroed by some other task.
     * @param latch Latch to wait for.
     * @param latchName Name to be used in log messages for the given latch.
     */
    private void waitForLatchUnlock(CountDownLatch latch, String latchName) {
        try {

            latch.await();

        } catch (InterruptedException ie) {
            System.out.println("Warning: continuing before " + latchName + " latch was released");
        }
    }

}

