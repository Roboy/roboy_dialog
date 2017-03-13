package roboy.memory;

import java.util.List;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;


public class RoboyMind implements Memory 
{

	private Ros ros;

    public RoboyMind(Ros ros_)
	{
		ros = ros_;
	}

	private ServiceResponse CreateInstance(String object_class, int id)
	{
		Service CreateInstanceSrv = new Service(ros, "/roboy_mind/create_instance", "/roboy_mind/create_instance");
		String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  id + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse AssertProperty(String object, String property, String instance, boolean data)
	{
		Service AssertPropertySrv = new Service(ros, "/roboy_mind/assert_property", "/roboy_mind/assert_property");
		String params = "{\"object\": " + "\"" + object + "\", \"property\": \"" +  property + "\", \"instance\": \"" + instance + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = AssertPropertySrv.callServiceAndWait(request);
		return response;
	}

	private boolean FindInstances(String property, String value, boolean data)
	{
		Service FindInstancesSrv = new Service(ros, "/roboy_mind/find_instances", "/roboy_mind/find_instances");
		String params = "{\"property\": " + "\"" + property + "\", \"value\": \"" + value + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		response = FindInstancesSrv.callServiceAndWait(request);
		return response;
	}

	private boolean ShowInstances(String object_class)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/show_instances", "/roboy_mind/show_instances");
		String params = "{\"object_class\": " + "\"" + object_class + "\"}";
		ServiceRequest request = new ServiceRequest(params);
		response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}

	@Override
	public boolean save() 
	{
		return true;
	}
	
	@Override
	public boolean retrieve()
	{
		return true;
	}
	
}