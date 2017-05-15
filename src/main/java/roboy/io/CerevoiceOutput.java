package roboy.io;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import org.ros.node.*;
import roboy.dialog.action.Action;
import roboy.dialog.action.FaceAction;
import roboy.dialog.action.SpeechAction;
import roboy.util.Ros;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;
import roboy.util.Ros;

import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

/**
 * Cerevoice text to speech + Roboy's facial expressions.
 */
public class CerevoiceOutput extends AbstractNodeMain implements OutputDevice
{
	private EmotionOutput emotion;
	public CerevoiceOutput(EmotionOutput emotion)
	{
		this.emotion = emotion;
	}

	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				final String textToSay = ((SpeechAction) a).getText();
				final int duration = ((SpeechAction) a).getText().length()==0 ? 0 : 1 + ((SpeechAction) a).getText().length()/8;
				Runnable r = new Runnable() {
					@Override
					public void run() {
						emotion.act(new FaceAction("speak", duration));
					}
				};

				Thread t = new Thread(r);
				t.start();
				say(textToSay);
//				emotion.act(new FaceAction("neutral"));
			}
		}
	}
	
	public void say(String text)
	{
	    Service CerevoiceTTS = new Service(Ros.getInstance(), "/speech_synthesis/talk", "/speech_synthesis/Talk");
	    ServiceRequest request = new ServiceRequest("{\"text\": " + "\"" + text + "\"}");
	    CerevoiceTTS.callServiceAndWait(request);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("speech_synthesis/client");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		ServiceClient<speech_synthesis_srvs.TalkRequest, speech_synthesis_srvs.TalkResponse> serviceClient;
		try {
			serviceClient = connectedNode.newServiceClient("/speech_synthesis/talk", speech_synthesis_srvs.Talk._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
		final speech_synthesis_srvs.TalkRequest request = serviceClient.newMessage();
		request.setText("hello");

		serviceClient.call(request, new ServiceResponseListener<speech_synthesis_srvs.TalkResponse>() {
			@Override
			public void onSuccess(speech_synthesis_srvs.TalkResponse response) {
				connectedNode.getLog().info(
						String.format("ROS call returns ", response.getSuccess()));
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});
	}

	public static void main(String[] args)  {

		String hostName = "10.177.255.161";
		URI masterURI = URI.create("http://10.177.255.161:11311");

		NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(hostName);
		nodeConfiguration.setMasterUri(masterURI);

		// Create and start Tango ROS Node
		nodeConfiguration.setNodeName("speech_synthesis/client");
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
		nodeMainExecutor.execute(new AbstractNodeMain() {
			@Override
			public GraphName getDefaultNodeName() {
				return GraphName.of("speech_synthesis/client");
			}

		}, nodeConfiguration);

	}
}
