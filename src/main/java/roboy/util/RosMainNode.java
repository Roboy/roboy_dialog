package roboy.util;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import roboy_communication_cognition.*;

import java.net.URI;

public class RosMainNode extends AbstractNodeMain {

    private static RosMainNode node;
    private ServiceClient<TalkRequest, TalkResponse> speechSynthesisClient;
    private ServiceClient<GenerateAnswerRequest, GenerateAnswerResponse> generativeClient;
    private ServiceClient<DetectFaceRequest, DetectFaceResponse> faceDetectionClient;
    private ServiceClient<RecognizeObjectRequest, RecognizeObjectResponse> objectRecognitionRequest;
    private ServiceClient<RecognizeSpeechRequest, RecognizeSpeechResponse> sttClient;

    private RosMainNode()
    {
        // start ROS nodes
        String hostName = System.getenv("ROS_HOSTNAME");
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
//            generativeClient = connectedNode.newServiceClient("/roboy/cognition/gnlp/predict", GenerateAnswer._TYPE);
//            faceDetectionClient = connectedNode.newServiceClient("/speech_synthesis/talk", DetectFace._TYPE);
//            objectRecognitionRequest = connectedNode.newServiceClient("/speech_synthesis/talk", RecognizeObject._TYPE);
//            sttClient = connectedNode.newServiceClient("/roboy/cognition/speech/recognition", RecognizeSpeech._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }

    }
    private boolean response;
    private void getResponse(boolean r)
    {
        this.response = r;
    }
    public void SynthesizeSpeech(String text)
    {
        TalkRequest request = speechSynthesisClient.newMessage();
        request.setText(text);
        ServiceResponseListener<TalkResponse> listener = new ServiceResponseListener<TalkResponse>() {
            @Override
            public void onSuccess(TalkResponse response) {
                getResponse(response.getSuccess());
                System.out.println(String.format("ROS call returns ", response.getSuccess()));
            }

            @Override
            public void onFailure(RemoteException e) {
                throw new RosRuntimeException(e);
            }

        };
        speechSynthesisClient.call(request,  listener);
        System.out.println(response);
    }

}

