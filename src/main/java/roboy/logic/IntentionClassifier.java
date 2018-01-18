package roboy.logic;

import java.util.List;
import java.util.Map;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;


public class IntentionClassifier 
{

	private Ros ros;

    public IntentionClassifier(Ros ros_)
	{
		this.ros = ros_;
	}

	public String classify(String utterance)
	{
		Service CreateInstanceSrv = new Service(this.ros, "/roboy_mind/classify_intention", "/roboy_mind/classify_intention");
		String params = "{\"utterance\": " + "\"" + utterance + "\"}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		String intent  = response.toJsonObject().getString("intent");
		return intent;
	}

}