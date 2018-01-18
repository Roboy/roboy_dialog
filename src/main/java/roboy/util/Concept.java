package roboy.util;

import java.util.Map;
import java.util.HashMap;
import roboy.memory.RoboyMind;

import roboy.linguistics.Linguistics;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

/**
 * Protege memory concept
 */
public class Concept {
	
	private Map<String, Object> attributes;

	public Concept()
	{
		this.attributes = new HashMap<>();
		this.attributes.put("class_name", "Person"); //TODO remove hardcoded person constructor
		this.attributes.put("id", RoboyMind.getInstance().object_id++);
	}

	public Concept(Map<String, Object> attrs)
	{
		this.attributes = new HashMap<>();
		this.attributes.put("class_name", "Person");
		this.attributes.put("id", RoboyMind.getInstance().object_id++);
		for (Map.Entry<String,Object> attr: attrs.entrySet())
		{
			this.attributes.put(attr.getKey(),attr.getValue());
		}
	}
	
	public Concept(String name){
		this.attributes = new HashMap<>();
		attributes.put(Linguistics.NAME, name);
		this.attributes.put("id", RoboyMind.getInstance().object_id++);
	}

	public void addAttribute(String property, Object value)
	{
		this.attributes.put(property, value);
	}

	public void addAttributes(Map<String, Object> attrs)
	{
		this.attributes.putAll(attrs);
	}

	public Map<String, Object> getAttributes()
	{
		return this.attributes;
	}
	
	public Object getAttribute(String key){
		return attributes.get(key);
	}

	public String getProperties() 
	{
		StringBuilder properties = new StringBuilder();

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
//		    if (attribute.getKey() != "class_name" && attribute.getKey() != "id")
//		    {
		    	properties.append(attribute.getKey()).append(",");
//		    }
		}

		if (properties.length() > 0 && properties.charAt(properties.length()-1) == ',')
		{
			properties = new StringBuilder(properties.substring(0, properties.length() - 1));
		}

		return properties.toString();

	}

	public String getValues() 
	{
		
		StringBuilder values = new StringBuilder();

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
		    if ( ! attribute.getKey().equals("class") && ! attribute.getKey().equals("id"))
		    {
		    	values.append(attribute.getValue().toString()).append(",");
		    }
		    
		}

		if (values.length()>0 && values.charAt(values.length()-1)==',')
		{
			values = new StringBuilder(values.substring(0, values.length() - 1));
		}

		return values.toString();
	}
	
	public boolean hasAttribute(String property)
	{
		return ! this.getProperties().equals("") && this.getProperties().contains(property);
	}

	public Object retrieve()
	{
		return RoboyMind.getInstance().retrieve(this);
	}

	public boolean updateInMemory()
	{
		if (RoboyMind.getInstance().retrieve(this).size()==0)
		{
			RoboyMind.getInstance().save(this);
		}
		return RoboyMind.getInstance().update(this);
	}

	public int getID()
	{
		return Integer.parseInt(this.getAttribute("id").toString().replace("\"", ""));
	}

	// TODO getClass


}
