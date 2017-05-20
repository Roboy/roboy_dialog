package roboy.io;

import java.util.List;
import org.ros.node.*;
import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;


import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

/**
 * Cerevoice text to speech
 */
public class CerevoiceOutput extends AbstractNodeMain implements OutputDevice
{

	private ServiceClient<speech_synthesis_srvs.TalkRequest, speech_synthesis_srvs.TalkResponse> cerevoiceServiceClient;
	@Override
	public void act(List<Action> actions) {
		for(Action a : actions){
			if(a instanceof SpeechAction){
				final String textToSay = ((SpeechAction) a).getText();
				say(textToSay);
			}
		}
	}
	
	public void say(String text)
	{
		final speech_synthesis_srvs.TalkRequest request = cerevoiceServiceClient.newMessage();
		request.setText(text);

		cerevoiceServiceClient.call(request, new ServiceResponseListener<speech_synthesis_srvs.TalkResponse>() {
			@Override
			public void onSuccess(speech_synthesis_srvs.TalkResponse response) {
				System.out.println(String.format("ROS call returns ", response.getSuccess()));
			}

			@Override
			public void onFailure(RemoteException e) {
				throw new RosRuntimeException(e);
			}
		});

	}

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("speech_synthesis/client");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		try {
			cerevoiceServiceClient = connectedNode.newServiceClient("/speech_synthesis/talk", speech_synthesis_srvs.Talk._TYPE);
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}

	}
}
