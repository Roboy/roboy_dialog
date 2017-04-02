package roboy.memory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.json.JsonReader;

import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

import roboy.util.Concept;
import roboy.util.Relation;
import roboy.util.Ros;

public class RoboyMind implements Memory<Concept>
{

	private ServiceResponse CreateInstance(String object_class, int object_id)
	{
		Service CreateInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/create_instance", "/roboy_mind/create_instance");
		String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  object_id + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse AssertProperty(String object, String property, Object instance, boolean data)
	{
		Service AssertPropertySrv = new Service(Ros.getInstance(), "/roboy_mind/assert_property", "/roboy_mind/assert_property");
		String params = "{\"object\": " + "\"" + object + "\", \"property\": \"" +  property + "\", \"instance\": \"" + String.valueOf(instance) + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = AssertPropertySrv.callServiceAndWait(request);
		return response;
	}

	private ServiceResponse FindInstances(String property, String value)
	{
		Service FindInstancesSrv = new Service(Ros.getInstance(), "/roboy_mind/find_instances", "/roboy_mind/find_instances");
		
		JsonObject params = Json.createObjectBuilder()
	     .add("property", property)
	     .add("value", value)
	     .build();

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = FindInstancesSrv.callServiceAndWait(request);
		// System.out.println(response.toString());
		return response;
	}

	private JsonObject ListAttributes(String object)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/show_property", "/roboy_mind/show_property");

		JsonObject params = Json.createObjectBuilder()
	     .add("object", object.replace("\"", ""))
	     .build();

		// System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		// System.out.println(response.toString());


		JsonReader jsonReader = Json.createReader(new StringReader(response.toJsonObject().getString("property")));
		JsonObject attributes = jsonReader.readObject();
		jsonReader.close();

		return attributes;
	}

	private ServiceResponse SaveObject(String object_class, int object_id, String properties, String values)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/save_object", "/roboy_mind/save_object");

		JsonObject params = Json.createObjectBuilder()
	     .add("class_name", object_class)
	     .add("id", object_id)
	     .add("properties", properties)
	     .add("values", values)
	     .build();

		// System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private Concept GetObject(String properties, String values)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/get_object", "/roboy_mind/get_object");

		JsonObject params = Json.createObjectBuilder()
	     .add("properties", properties)
	     .add("values", values)
	     .build();

		// System.out.println(params);

		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);

		// System.out.println("Get object service response:");
		// System.out.println(response.toString());

		Concept result = new Concept();
		result.addAttribute("class_name", response.toJsonObject().getString("class_name"));
		// String instance = response.toJsonObject().getString("instance");
		result.addAttribute("instance", response.toJsonObject().getString("instance"));

		return result;
	}

	private ServiceResponse ShowInstance(String object_class)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/show_instances", "/roboy_mind/show_instances");
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
	public List<Concept> retrieve(Concept object)
	{
		// get objects matching the requested attributes
		String properties = object.getProperties();
		String values = object.getValues();

		JsonArray instances = FindInstances(properties, values).toJsonObject().getJsonArray("instances");
		
		List<Concept> result = new ArrayList();

		// get these objects' attributes
		for (JsonValue i: instances)
		{
			JsonObject attributes = ListAttributes(i.toString());
			Concept instance = new Concept();

			for (Map.Entry<String, JsonValue> entry : attributes.entrySet())
			{
			    instance.addAttribute(entry.getKey(), entry.getValue());
			}

			result.add(instance);
		}
		
		return result;
	}
	
}