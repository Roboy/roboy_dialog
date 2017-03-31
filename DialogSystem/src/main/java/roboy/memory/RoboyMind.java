package roboy.memory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.JsonReader;

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

	private ServiceResponse FindInstances(String property, String value)
	{
		Service FindInstancesSrv = new Service(this.ros, "/roboy_mind/find_instances", "/roboy_mind/find_instances");
		
		JsonObject params = Json.createObjectBuilder()
	     .add("property", property)
	     .add("value", value)
	     .build();

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = FindInstancesSrv.callServiceAndWait(request);
		System.out.println(response.toString());
		return response;
	}

	private JsonObject ListAttributes(String object)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/show_property", "/roboy_mind/show_property");

		JsonObject params = Json.createObjectBuilder()
	     .add("object", object)
	     .build();

		System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		System.out.println(response.toString());


		JsonReader jsonReader = Json.createReader(new StringReader(response.toJsonObject().getString("property")));
		JsonObject attributes = jsonReader.readObject();
		jsonReader.close();

		return attributes;
	}

	private ServiceResponse SaveObject(String object_class, int object_id, String properties, String values)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/save_object", "/roboy_mind/save_object");

		JsonObject params = Json.createObjectBuilder()
	     .add("class_name", object_class)
	     .add("id", object_id)
	     .add("properties", properties)
	     .add("values", values)
	     .build();

		System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private Concept GetObject(String properties, String values)
	{
		Service ShowInstanceSrv = new Service(ros, "/roboy_mind/get_object", "/roboy_mind/get_object");

		JsonObject params = Json.createObjectBuilder()
	     .add("properties", properties)
	     .add("values", values)
	     .build();

		System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);

		System.out.println("Get object service response:");
		System.out.println(response.toString());

		Concept result = new Concept();
		result.addAttribute("class_name", response.toJsonObject().getString("class_name"));
		// String instance = response.toJsonObject().getString("instance");
		result.addAttribute("instance", response.toJsonObject().getString("instance"));

		return result;
	}

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
		String object_class = object.getAttributes().get("class_name").toString();
		int object_id = (int) object.getAttributes().get("id");
		
		String properties = object.getProperties();
		String values = object.getValues();
		
		ServiceResponse srvCall = SaveObject(object_class, object_id, properties, values);

		return srvCall.getResult();
	}
	
	@Override
	public Concept retrieve(Concept object)
	{
		// get the object

		String properties = object.getProperties();
		String values = object.getValues();

		FindInstances(properties, values);

		// Concept objectOfInterest = GetObject(properties, values);

		// // get attributes
		// String instance = objectOfInterest.getAttribute("instance").toString();
		// for (Map.Entry<String, JsonValue> entry : ListAttributes(instance).entrySet())
		// {
		//     object.addAttribute(entry.getKey(), entry.getValue());
		// }

		return object;
	}
	
}