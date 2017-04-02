package roboy.util;

import java.util.Map;
import java.util.HashMap;
import roboy.memory.RoboyMind;

import roboy.linguistics.Linguistics;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

public class Concept {
	
	private Map<String, Object> attributes;

	public Concept()
	{
		this.attributes = new HashMap<String, Object>();
	}

	public Concept(Map<String, Object> attrs)
	{
		this.attributes = new HashMap<String, Object>();
		this.addAttributes(attrs);
		this.memorize();
	}
	
	public Concept(String name){
		this();
		attributes.put(Linguistics.NAME, name);
		this.updateMemory();
	}

	public void addAttribute(String property, Object value)
	{
		this.attributes.put(property, value);
		this.updateMemory();
	}

	public void addAttributes(Map<String, Object> attrs)
	{
		this.attributes.putAll(attrs);
		this.updateMemory();
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
		String properties = ""; 

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	properties +=  attribute.getKey() + ",";
		    }
		}

		if (properties.length()>0 && properties.charAt(properties.length()-1)==',')
		{
			properties = properties.substring(0, properties.length()-1);
		}

		return properties;

	}

	public String getValues() 
	{
		
		String values = "";

		for (Map.Entry<String, Object> attribute : this.getAttributes().entrySet())
		{
		    if (attribute.getKey() != "class" && attribute.getKey() != "id")
		    {
		    	values +=  attribute.getValue().toString() + ",";
		    }
		    
		}

		if (values.length()>0 && values.charAt(values.length()-1)==',')
		{
			values = values.substring(0, values.length()-1);
		}

		return values;
	}
	
	public boolean hasAttribute(String property)
	{
		if (this.getProperties() != "" && this.getProperties().contains(property))
		{
			return true;
		}
		return false;
	}

	public boolean memorize()
	{
		return RoboyMind.getInstance().save(this);
	}

	public Object retrieve()
	{
		return RoboyMind.getInstance().retrieve(this);
	}

	public boolean updateMemory()
	{
		return RoboyMind.getInstance().update(this);
	}

	// TODO getClass
	// TODO getID

}
