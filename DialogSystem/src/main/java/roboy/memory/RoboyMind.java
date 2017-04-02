package roboy.memory;

import java.util.List;
import java.util.Map;
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
import roboy.util.Ros;

public class RoboyMind implements Memory<Concept>
{
	private static RoboyMind roboyMemory;
	private int object_id = -1;
	private RoboyMind()
	{
	}

	public static RoboyMind getInstance()
	{
		if (roboyMemory == null)
		{
			roboyMemory =  new RoboyMind();
		}
		return roboyMemory;
	}
	private ServiceResponse CreateInstance(String object_class, int object_id)
	{
		Service CreateInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/create_instance", "/roboy_mind/create_instance");
		String params = "{\"object_class\": " + "\"" + object_class + "\", \"id\": " +  object_id + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private boolean AssertProperty(String object, String property, String value, boolean data)
	{
		Service AssertPropertySrv = new Service(Ros.getInstance(), "/roboy_mind/assert_property", "/roboy_mind/assert_property");

		JsonObject params = Json.createObjectBuilder()
				.add("object", object.replace("\"", ""))
				.add("property", property)
				.add("instance", value)
				.add("data", String.valueOf(data))
				.build();

//		String params = "{\"object\": " + "\"" + object + "\", \"property\": \"" +  property + "\", \"instance\": \"" + String.valueOf(instance) + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
		boolean response = AssertPropertySrv.callServiceAndWait(request).toJsonObject().getBoolean("success");
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

	private ServiceResponse SaveObject(String object_class, String properties, String values, int object_id)
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
		
		String properties = object.getProperties();
		String values = object.getValues();

		object.addAttribute("id", this.object_id++);
		
		ServiceResponse srvCall = SaveObject(object_class, properties, values, this.object_id++);

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

	public boolean update(Concept object) // requires having attributes id and class_name
	{
		String object_name = object.getAttribute("object_class").toString() + "_" + object.getAttribute("id");
		List<Concept> saved_objects = RoboyMind.getInstance().retrieve(object);
		if (saved_objects.size() == 0)
		{
			System.out.println("Cannot update properties. Memory does not contains the requested object yet.");
			return false;
		}
		else if(saved_objects.size()>1)
		{
			System.out.println("Multiple objects with the requested properties are store in memory. Cannot update");
			return false;
		}
		else
		{
			String[] propertiesToUpdate = object.getProperties().split(",");
			String[] valuesToUpdate = object.getValues().split(",");
			int numOfPropsUpdated = 0;
			for (int i=0; i<propertiesToUpdate.length; i++)
			{
				boolean updated = AssertProperty(object_name, propertiesToUpdate[i], valuesToUpdate[i], false);
				if (updated)
				{
					numOfPropsUpdated++;
				}

				if (i==propertiesToUpdate.length && numOfPropsUpdated==propertiesToUpdate.length)
				{
					return true;
				}
			}

			return false;
		}

	}
	
}