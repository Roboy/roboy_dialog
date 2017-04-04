package roboy.memory;

import java.util.HashMap;
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
	public int object_id = 0;
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
	private ServiceResponse CreateInstance(String class_name, int object_id)
	{
		Service CreateInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/create_instance", "/roboy_mind/create_instance");
		String params = "{\"class_name\": " + "\"" + class_name + "\", \"id\": " +  object_id + "}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = CreateInstanceSrv.callServiceAndWait(request);
		return response;
	}

	private boolean AssertProperty(String object, String property, String value)
	{
		Service AssertPropertySrv = new Service(Ros.getInstance(), "/roboy_mind/assert_property", "/roboy_mind/assert_property");

		JsonObject params = Json.createObjectBuilder()
				.add("object", object.replace("\"", ""))
				.add("property", property)
				.add("instance", value)
				.build();

//		String params = "{\"object\": " + "\"" + object + "\", \"property\": \"" +  property + "\", \"instance\": \"" + String.valueOf(instance) + "\", \"data\": " + String.valueOf(data) + "}";
		ServiceRequest request = new ServiceRequest(params);
//		ServiceResponse response = AssertPropertySrv.callServiceAndWait(request);
		return AssertPropertySrv.callServiceAndWait(request).getResult();
	}

	private List<Concept> FindInstances(String property, String value)
	{
		Service FindInstancesSrv = new Service(Ros.getInstance(), "/roboy_mind/find_instances", "/roboy_mind/find_instances");
		
		JsonObject params = Json.createObjectBuilder()
	     .add("property", property)
	     .add("value", value)
	     .build();

		ServiceRequest request = new ServiceRequest(params);
		JsonArray response = FindInstancesSrv.callServiceAndWait(request).toJsonObject().getJsonArray("instances");

		List<Concept> result = new ArrayList();

		// get these objects' attributes
		for (JsonValue i: response)
		{
			JsonObject attributes = ListAttributes(i.toString());
			Concept instance = new Concept();

			for (Map.Entry<String, JsonValue> entry : attributes.entrySet()) {
				instance.addAttribute(entry.getKey(), entry.getValue());
			}

			result.add(instance);

		}
		return result;
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

	private ServiceResponse SaveObject(String class_name, String properties, String values, int object_id)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/save_object", "/roboy_mind/save_object");
		JsonObject params = Json.createObjectBuilder()
	     .add("class_name", class_name)
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

	private ServiceResponse ShowInstance(String class_name)
	{
		Service ShowInstanceSrv = new Service(Ros.getInstance(), "/roboy_mind/show_instances", "/roboy_mind/show_instances");
		String params = "{\"class_name\": " + "\"" + class_name + "\"}";
		ServiceRequest request = new ServiceRequest(params);
		ServiceResponse response = ShowInstanceSrv.callServiceAndWait(request);
		return response;
	}


	@Override
	public boolean save(Concept object)  throws NullPointerException
	{

		//create an object
		try
		{
			String class_name = object.getAttributes().get("class_name").toString();
			int object_id = (int) object.getAttribute("id");
			String properties = object.getProperties();
			String values = object.getValues();

			ServiceResponse srvCall = SaveObject(class_name, properties, values, object_id);

			return srvCall.getResult();
		}
		catch (NullPointerException e)
		{
			System.out.println("The object you are trying to save does not have an attribute which is required (id or class_name) ");
			return false;
		}
		
		
	}
	
	@Override
	public List<Concept> retrieve(Concept object)
	{
		// get objects matching the requested attributes
		String properties = "id";
		String values = object.getAttribute("id").toString();

		List<Concept> result = FindInstances(properties, values);
		
		return result;
	}

	public boolean update(Concept object) // requires having attributes id and class_name
	{
		String object_name = object.getAttribute("class_name").toString() + "_" + object.getAttribute("id");
		List<Concept> saved_objects = RoboyMind.getInstance().retrieve(object);
		if (saved_objects.size() == 0)
		{
			System.out.println("Cannot update properties. Memory does not contain the requested object yet.");
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
				boolean updated = AssertProperty(object_name, propertiesToUpdate[i], valuesToUpdate[i]);
				if (updated)
				{
					numOfPropsUpdated++;
				}

				if (i+1==propertiesToUpdate.length && numOfPropsUpdated==propertiesToUpdate.length)
				{
					return true;
				}
			}

			return false;
		}

	}

	public Map<String,List<Concept>> match (Concept object)
	{
		Map<String,List<Concept>> result = new HashMap<>();
		List<Concept> matchedConcepts = new ArrayList<>();

		for (Map.Entry<String, Object> attribute: object.getAttributes().entrySet())
		{
			if (!attribute.getKey().equals("id") && !attribute.getKey().equals("class_name"))
			{
				List<Concept> matches = FindInstances(attribute.getKey(), attribute.getValue().toString());

				if (!matches.isEmpty())
				{
					for (Concept match: matches)
					{
						if(match.getID()!=object.getID())
						{
							matchedConcepts.add(match);
						}
					}
				}
				if (!matchedConcepts.isEmpty()) {
					result.put(attribute.getKey(), matchedConcepts);
				}
			}

		}

		return result;
	}
	
}