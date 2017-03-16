package roboy.memory;

import java.util.List;
import java.util.Map;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import roboy.util.Concept;
import roboy.util.Relation;

public class RoboyMind implements Memory 
{

	private Ros ros;

    public RoboyMind(Ros ros_)
	{
		this.ros = ros_;
	}

	private ServiceResponse CreateInstance(String object_class, int object_id)
	{
		Service CreateInstanceSrv = new Service(this.ros, "/roboy_mind/create_instance", "/roboy_mind/create_instance");
		String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  object_id + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse AssertProperty(String object, String property, Object instance, boolean data)
	{
		Service AssertPropertySrv = new Service(this.ros, "/roboy_mind/assert_property", "/roboy_mind/assert_property");
		String params = "{\"object\": " + "\"" + object + "\", \"property\": \"" +  property + "\", \"instance\": \"" + String.valueOf(instance) + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = AssertPropertySrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse FindInstances(String property, String value, boolean data)
	{
		Service FindInstancesSrv = new Service(this.ros, "/roboy_mind/find_instances", "/roboy_mind/find_instances");
		String params = "{\"property\": " + "\"" + property + "\", \"value\": \"" + value + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = FindInstancesSrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse ShowInstances(String object_class)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/show_instances", "/roboy_mind/show_instances");
		String params = "{\"object_class\": " + "\"" + object_class + "\"}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}

	@Override
	public boolean save(Concept object) 
	{
		//TODO combine both services on the Python side
		//create an object
		String object_class = object.getAttributes().get("class").toString();
		int object_id = (int) object.getAttributes().get("id");
		CreateInstance(object_class, object_id);
		String object_name = object_class + object_id;
		
		//assign attributes
		for (Map.Entry<String, Object> attribute : object.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	String property = attribute.getKey();
		    	Object instance = attribute.getValue();
		    	AssertProperty(object_name,  property,  instance,  true);
		    }
			
		}
		//TODO check the service response
		return true;
	}
	
	@Override
	public boolean retrieve(Concept object)
	{
		// TODO implement service on the python side
		return true;
	}
	
	@Override
	public boolean save(Relation object) 
	{
		// TODO define relations in Python roboy_mind
		return false;
	}
	
	@Override
	public boolean retrieve(Relation object) 
	{
		// TODO define relations in Python roboy_mind
		return false;
	}
	
}