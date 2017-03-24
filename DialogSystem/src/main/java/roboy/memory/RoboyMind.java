package roboy.memory;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import roboy.util.Concept;
import roboy.util.Relation;

public class RoboyMind implements Memory<Concept>
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

	private ServiceResponse SaveObject(String object_class, int object_id, String properties, String values)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/save_object", "/roboy_mind/save_object");

		JsonObject params = Json.createObjectBuilder()
	     .add("object_class", "object_class")
	     .add("id", object_id)
	     .add("properties", properties)
	     .add("values", values)
	     .build();

		// String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  object_id + ", \"properties\": \"" +  properties + "\", \"values\": \"" +  values + "\"}";

		System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}

	// private Concept GetObject(String object_class, int object_id, String properties, String values)
	// {
	// 	Service ShowInstanceSrv = new Service(ros, "/roboy_mind/save_object", "/roboy_mind/save_object");

	// 	JsonObject params = Json.createObjectBuilder()
	//      .add("object_class", "object_class")
	//      .add("id", object_id)
	//      .add("properties", properties)
	//      .add("values", values)
	//      .build();

	// 	// String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  object_id + ", \"properties\": \"" +  properties + "\", \"values\": \"" +  values + "\"}";

	// 	System.out.println(params);

	// 	ServiceRequest request = new ServiceRequest(params);
	// 	ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
	// 	return response;
	// }

	private ServiceResponse ShowInstance(String object_class)
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

		//create an object
		String object_class = object.getAttributes().get("class").toString();
		int object_id = (int) object.getAttributes().get("id");
		
		String properties = ""; 
		String values = "";

		//separate values and attributes
		for (Map.Entry<String, Object> attribute : object.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	// valuesList.add(attribute.getValue().toString());
		    	// propertiesList.add(attribute.getKey());
		    	properties += "\n- \'" + attribute.getKey() + "\'";
		    	values += "\n- \'" + attribute.getValue().toString() + "\'";
		    }
		}

		SaveObject(object_class, object_id, properties, values);

		//TODO check the service response
		return true;
	}
	
	@Override
	public Concept retrieve(Concept object)
	{
		// TODO implement service on the python side
		return object;
	}
	
	// @Override
	// public boolean save(Relation object) 
	// {
	// 	// TODO define relations in Python roboy_mind
	// 	return false;
	// }
	
	// @Override
	// public boolean retrieve(Relation object) 
	// {
	// 	// TODO define relations in Python roboy_mind
	// 	return false;
	// }
	
}