package roboy.util;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.internal.message.RawMessage;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import roboy_communication_cognition.*;
import roboy_communication_control.*;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class RosMainNode extends AbstractNodeMain {

    private static RosMainNode node;
    private CountDownLatch rosConnectionLatch;
    private ServiceClient<TalkRequest, TalkResponse> speechSynthesisClient;
    private ServiceClient<GenerateAnswerRequest, GenerateAnswerResponse> generativeClient;
//    private ServiceClient<DetectFaceRequest, DetectFaceResponse> faceDetectionClient;
//    private ServiceClient<RecognizeObjectRequest, RecognizeObjectResponse> objectRecognitionRequest;
    private ServiceClient<RecognizeSpeechRequest, RecognizeSpeechResponse> sttClient;
    private ServiceClient<ShowEmotionRequest, ShowEmotionResponse> emotionClient;
    protected Object resp;

    private RosMainNode()
    {
        // start ROS nodes
        String hostName = System.getenv("ROS_HOSTNAME");
        if (hostName.isEmpty())
        {
            System.out.println("Could not find ROS hostname. ROS will be unavailable. Set ROS_HOSTNAME environmental variable.");
        }

        URI masterURI = URI.create("http://" + hostName + ":11311");

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(hostName);
        nodeConfiguration.setMasterUri(masterURI);

        // Create and start ROS Node
        nodeConfiguration.setNodeName("roboy_dialog");
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(this, nodeConfiguration);

        node = this;
    }

    public static RosMainNode getInstance() {
       if (node==null) {
           new RosMainNode();
       }
       return node;
    }



    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("roboy_dialog");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        try {
            speechSynthesisClient = connectedNode.newServiceClient("/roboy/cognition/speech/synthesis/talk", Talk._TYPE);
            generativeClient = connectedNode.newServiceClient("/roboy/cognition/generative_nlp/answer", GenerateAnswer._TYPE);
//            faceDetectionClient = connectedNode.newServiceClient("/speech_synthesis/talk", DetectFace._TYPE);
//            objectRecognitionRequest = connectedNode.newServiceClient("/speech_synthesis/talk", RecognizeObject._TYPE);
            sttClient = connectedNode.newServiceClient("/roboy/cognition/speech/recognition", RecognizeSpeech._TYPE);
            emotionClient = connectedNode.newServiceClient("/roboy/control/face/emotion", ShowEmotion._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }

    }

    public boolean SynthesizeSpeech(String text)
    {
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

    public String RecognizeSpeech()
    {
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

    public String GenerateAnswer(String question)
    {
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
        waitForLatchUnlock(rosConnectionLatch, sttClient.getName().toString());
        return ((String) resp);
    }

    public boolean ShowEmotion(String emotion)
    {
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
        waitForLatchUnlock(rosConnectionLatch, sttClient.getName().toString());
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
            System.out.println("Warning: continuing before " + latchName + " latch was released");
        }
    }

}

